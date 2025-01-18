package monster.monkeyking.chapter01

typealias Plays = Map<String, Play>

data class Play(
    val name: String,
    val type: String
)

data class Performance(
    val playID: String,
    val audience: Int
)

data class PerformanceEnriched(
    val play: Play,
    val audience: Int
)

data class Invoice(
    val customer: String,
    val performances: List<Performance>
)

data class StatementData(
    val customer: String,
    val performances: List<PerformanceEnriched>,
    val plays: Plays
)

fun statement(invoice: Invoice, plays: Plays): String {
    fun playFor(aPerformance: Performance): Play {
        return plays[aPerformance.playID] ?: error("알 수 없는 장르: ${aPerformance.playID}")
    }

    fun enrichPerformance(aPerformance: Performance): PerformanceEnriched {
        return PerformanceEnriched(
            play = playFor(aPerformance),
            audience = aPerformance.audience
        )
    }

    val statementData = StatementData(
        customer = invoice.customer,
        performances = invoice.performances.map { enrichPerformance(it) },
        plays = plays
    )
    return renderPlainText(statementData)
}

private fun renderPlainText(data: StatementData): String {
    fun usd(aNumber: Int): String {
        return "$${aNumber / 100}.00"
    }

    fun amountFor(aPerformance: PerformanceEnriched): Int {
        var result = 0
        when (aPerformance.play.type) {
            "tragedy" -> {
                result = 40000
                if (aPerformance.audience > 30) {
                    result += 1000 * (aPerformance.audience - 30)
                }
            }

            "comedy" -> {
                result = 30000
                if (aPerformance.audience > 20) {
                    result += 10000 + 500 * (aPerformance.audience - 20)
                }
                result += 300 * aPerformance.audience
            }

            else -> error("알 수 없는 장르: ${aPerformance.play.type}")
        }
        return result
    }

    fun volumeCreditsFor(aPerformance: PerformanceEnriched): Int {
        var result = 0
        result += maxOf(aPerformance.audience - 30, 0)
        if ("comedy" == aPerformance.play.type) result += aPerformance.audience / 5
        return result
    }

    fun totalAmount(): Int {
        var totalAmount = 0
        for (perf in data.performances) {
            totalAmount += amountFor(perf)
        }
        return totalAmount
    }

    fun totalVolumeCredits(): Int {
        var result = 0
        for (perf in data.performances) {
            // 포인트를 적립한다.
            result += volumeCreditsFor(perf)
        }
        return result
    }

    var result = "청구 내역 (고객명: ${data.customer})\n"
    for (perf in data.performances) {
        // 청구 내역을 출력한다.
        result += "  ${perf.play.name}: ${usd(amountFor(perf))} (${perf.audience}석)\n"
    }
    result += "총액: ${usd(totalAmount())}\n"
    result += "적립 포인트: ${totalVolumeCredits()} 점\n"
    return result
}
