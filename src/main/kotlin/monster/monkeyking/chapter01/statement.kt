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

data class Invoice(
    val customer: String,
    val performances: List<Performance>
)

fun statement(invoice: Invoice, plays: Plays): String {
    var totalAmount = 0
    var volumeCredits = 0
    var result = "청구 내역 (고객명: ${invoice.customer})\n"
    val format = { amount: Int -> "$${amount / 100}.00" }

    fun amountFor(aPerformance: Performance, play: Play): Int {
        var result = 0
        when (play.type) {
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

            else -> error("알 수 없는 장르: ${play.type}")
        }
        return result
    }

    fun playFor(aPerformance: Performance): Play {
        return plays[aPerformance.playID] ?: error("알 수 없는 장르: ${aPerformance.playID}")
    }

    for (perf in invoice.performances) {
        val thisAmount = amountFor(perf, playFor(perf))

        // 포인트를 적립한다.
        volumeCredits += maxOf(perf.audience - 30, 0)
        // 희극 관객 5명마다 추가 포인트를 적립한다.
        if ("comedy" == playFor(perf).type) volumeCredits += perf.audience / 5

        // 청구 내역을 출력한다.
        result += "  ${playFor(perf).name}: ${format(thisAmount)} (${perf.audience}석)\n"
        totalAmount += thisAmount
    }

    result += "총액: ${format(totalAmount)}\n"
    result += "적립 포인트: $volumeCredits 점\n"
    return result
}
