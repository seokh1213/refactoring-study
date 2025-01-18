package monster.monkeyking.chapter01

class PerformanceCalculator(
    private val aPerformance: Performance,
    private val play: Play
) {
    val amount: Int
        get() {
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


}

fun createStatementData(
    plays: Plays,
    invoice: Invoice
): StatementData {
    fun playFor(aPerformance: Performance): Play {
        return plays[aPerformance.playID] ?: error("알 수 없는 장르: ${aPerformance.playID}")
    }

    fun volumeCreditsFor(aPerformance: PerformanceEnriched): Int {
        var result = 0
        result += maxOf(aPerformance.audience - 30, 0)
        if ("comedy" == aPerformance.play.type) result += aPerformance.audience / 5
        return result
    }

    fun totalAmount(data: List<PerformanceContext>): Int {
        return data.sumOf { it.amount }
    }

    fun totalVolumeCredits(data: List<PerformanceContext>): Int {
        return data.sumOf { it.volumeCredits }
    }

    fun enrichPerformance(aPerformance: Performance): PerformanceContext {
        val performanceCalculator = PerformanceCalculator(aPerformance, playFor(aPerformance))
        val aPerformanceEnriched = PerformanceEnriched(
            play = playFor(aPerformance),
            audience = aPerformance.audience
        )

        return PerformanceContext(
            play = playFor(aPerformance),
            audience = aPerformance.audience,
            amount = performanceCalculator.amount,
            volumeCredits = volumeCreditsFor(aPerformanceEnriched)
        )
    }

    val statementData = StatementData(
        customer = invoice.customer,
        performances = invoice.performances.map { enrichPerformance(it) },
        totalAmount = totalAmount(invoice.performances.map { enrichPerformance(it) }),
        totalVolumeCredits = totalVolumeCredits(invoice.performances.map { enrichPerformance(it) })
    )

    return statementData
}
