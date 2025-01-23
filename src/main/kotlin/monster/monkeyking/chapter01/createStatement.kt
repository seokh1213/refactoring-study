package monster.monkeyking.chapter01

fun createPerformanceCalculator(
    aPerformance: Performance,
    play: Play
): PerformanceCalculator {
    return when (play.type) {
        PlayType.TRAGEDY -> TragedyCalculator(aPerformance)
        PlayType.COMEDY -> ComedyCalculator(aPerformance)
    }
}

class TragedyCalculator(aPerformance: Performance) : PerformanceCalculator(aPerformance) {
    override val amount: Int = run {
        40000 + if (aPerformance.audience > 30) 1000 * (aPerformance.audience - 30) else 0
    }
}

class ComedyCalculator(aPerformance: Performance) : PerformanceCalculator(aPerformance) {
    override val amount: Int = run {
        30000 + 300 * aPerformance.audience + if (aPerformance.audience > 20) 10000 + 500 * (aPerformance.audience - 20) else 0
    }

    override val volumeCredits: Int = run {
        super.volumeCredits + aPerformance.audience / 5
    }
}

sealed class PerformanceCalculator(protected open val aPerformance: Performance) {
    abstract val amount: Int
    open val volumeCredits: Int
        get() {
            return maxOf(aPerformance.audience - 30, 0)
        }
}

fun createStatementData(
    plays: Plays,
    invoice: Invoice
): StatementData {
    fun playFor(aPerformance: Performance): Play {
        return plays[aPerformance.playID] ?: error("알 수 없는 장르: ${aPerformance.playID}")
    }

    fun totalAmount(data: List<PerformanceContext>): Int {
        return data.sumOf { it.amount }
    }

    fun totalVolumeCredits(data: List<PerformanceContext>): Int {
        return data.sumOf { it.volumeCredits }
    }

    fun enrichPerformance(aPerformance: Performance): PerformanceContext {
        val performanceCalculator = createPerformanceCalculator(aPerformance, playFor(aPerformance))

        return PerformanceContext(
            play = playFor(aPerformance),
            audience = aPerformance.audience,
            amount = performanceCalculator.amount,
            volumeCredits = performanceCalculator.volumeCredits
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
