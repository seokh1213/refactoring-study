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
        val play = plays.plays[perf.playID] ?: error("알 수 없는 장르: ${perf.playID}")
        var thisAmount = 0

        when (play.type) {
            "tragedy" -> {
                thisAmount = 40000
                if (perf.audience > 30) {
                    thisAmount += 1000 * (perf.audience - 30)
                }
            }
            "comedy" -> {
                thisAmount = 30000
                if (perf.audience > 20) {
                    thisAmount += 10000 + 500 * (perf.audience - 20)
                }
                thisAmount += 300 * perf.audience
            }
            else -> error("알 수 없는 장르: ${play.type}")
        }

        // 포인트를 적립한다.
        volumeCredits += maxOf(perf.audience - 30, 0)
        // 희극 관객 5명마다 추가 포인트를 적립한다.
        if ("comedy" == play.type) volumeCredits += perf.audience / 5

        // 청구 내역을 출력한다.
        result += "  ${play.name}: ${format(thisAmount)} (${perf.audience}석)\n"
        totalAmount += thisAmount
    }

    result += "총액: ${format(totalAmount)}\n"
    result += "적립 포인트: $volumeCredits 점\n"
    return result
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
