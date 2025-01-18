package monster.monkeyking.chapter01

fun createPerformanceCalculator(
    aPerformance: Performance,
    play: Play
): PerformanceCalculator {
    return when (play.type) {
        "tragedy" -> TragedyCalculator(aPerformance, play)
        "comedy" -> ComedyCalculator(aPerformance, play)
        else -> throw IllegalArgumentException("알 수 없는 장르: ${play.type}")
    }
}

class TragedyCalculator(private val aPerformance: Performance, private val play: Play) : PerformanceCalculator {
    override val amount: Int
        get() {
            var result = 40000
            if (aPerformance.audience > 30) {
                result += 1000 * (aPerformance.audience - 30)
            }
            return result
        }
    override val volumeCredits: Int
        get() {
            return maxOf(aPerformance.audience - 30, 0)
        }
}

class ComedyCalculator(private val aPerformance: Performance, private val play: Play) : PerformanceCalculator {

    override val amount: Int
        get() {
            var result = 30000
            if (aPerformance.audience > 20) {
                result += 10000 + 500 * (aPerformance.audience - 20)
            }
            result += 300 * aPerformance.audience
            return result
        }

    override val volumeCredits: Int
        get() {
            return maxOf(aPerformance.audience - 30, 0) + aPerformance.audience / 5
        }
}

interface PerformanceCalculator {
    val amount: Int
    val volumeCredits: Int
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
