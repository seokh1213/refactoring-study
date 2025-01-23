package monster.monkeyking.chapter01

fun createPerformanceCalculator(aPerformance: Performance, play: Play): PerformanceCalculator {
    return when (play.type) {
        PlayType.TRAGEDY -> TragedyCalculator(aPerformance)
        PlayType.COMEDY -> ComedyCalculator(aPerformance)
    }
}

class TragedyCalculator(aPerformance: Performance) : PerformanceCalculator {
    override val volumeCredits: Int = aPerformance.audience.coerceAtLeast(0) - 30

    override val amount: Int = run {
        val baseAmount = 40000
        val extraAmount = if (aPerformance.audience > 30) 1000 * (aPerformance.audience - 30) else 0

        return@run baseAmount + extraAmount
    }
}

class ComedyCalculator(aPerformance: Performance) : PerformanceCalculator {
    override val volumeCredits: Int = aPerformance.audience.coerceAtLeast(0) - 30 + aPerformance.audience / 5

    override val amount: Int = run {
        val baseAmount = 30000 + 300 * aPerformance.audience
        val extraAmount = if (aPerformance.audience > 20) 10000 + 500 * (aPerformance.audience - 20) else 0

        return@run baseAmount + extraAmount
    }
}

interface PerformanceCalculator {
    val amount: Int
    val volumeCredits: Int
}

fun createStatementData(plays: Plays, invoice: Invoice): StatementData {
    val enrichedPerformances = invoice.performances.enrichAll {
        plays[it.playID] ?: error("알 수 없는 장르: ${it.playID}")
    }

    val statementData = StatementData(
        customer = invoice.customer,
        performances = enrichedPerformances,
        totalAmount = enrichedPerformances.totalAmount(),
        totalVolumeCredits = enrichedPerformances.totalVolumeCredits()
    )

    return statementData
}

fun List<Performance>.enrichAll(playFor: (Performance) -> Play): EnrichedPerformances {
    return EnrichedPerformances(map { it.enrich(playFor(it)) })
}

fun Performance.enrich(play: Play): EnrichedPerformance {
    val performanceCalculator = createPerformanceCalculator(this, play)

    return EnrichedPerformance(
        play = play,
        audience = audience,
        amount = performanceCalculator.amount,
        volumeCredits = performanceCalculator.volumeCredits
    )
}

@JvmInline
value class EnrichedPerformances(private val performances: List<EnrichedPerformance>) :
    List<EnrichedPerformance> by performances {
    fun totalAmount(): Int {
        return performances.sumOf { it.amount }
    }

    fun totalVolumeCredits(): Int {
        return performances.sumOf { it.volumeCredits }
    }
}
