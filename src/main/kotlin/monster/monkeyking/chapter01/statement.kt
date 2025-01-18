package monster.monkeyking.chapter01

data class Plays(
    val plays: Map<String, Play>
)

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

fun statement(invoice: Invoice, plays: Plays ): String {
    var totalAmount = 0
    var volumeCredits = 0
    var result = "청구 내역 (고객명: ${invoice.customer})\n"
    val format = { amount: Int -> "$${amount / 100}.00" }

    for (perf in invoice.performances) {
        val thisAmount = amountFor(perf, plays.playFor(perf))

        // 포인트를 적립한다.
        volumeCredits += maxOf(perf.audience - 30, 0)
        // 희극 관객 5명마다 추가 포인트를 적립한다.
        if ("comedy" == plays.playFor(perf).type) volumeCredits += perf.audience / 5

        // 청구 내역을 출력한다.
        result += "  ${plays.playFor(perf).name}: ${format(thisAmount)} (${perf.audience}석)\n"
        totalAmount += thisAmount
    }

    result += "총액: ${format(totalAmount)}\n"
    result += "적립 포인트: $volumeCredits 점\n"
    return result
}

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

fun Plays.playFor(aPerformance: Performance): Play {
    return this.plays[aPerformance.playID] ?: error("알 수 없는 장르: ${aPerformance.playID}")
}

fun main() {
    val plays = Plays(
        mapOf(
            "hamlet" to Play("Hamlet", "tragedy"),
            "as-like" to Play("As You Like It", "comedy"),
            "othello" to Play("Othello", "tragedy")
        )
    )

    val invoice = Invoice(
        "BigCo",
        listOf(
            Performance("hamlet", 55),
            Performance("as-like", 35),
            Performance("othello", 40)
        )
    )

    println(statement(invoice, plays))
}
