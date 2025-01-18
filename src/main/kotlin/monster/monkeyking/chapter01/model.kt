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
    val audience: Int,
)

data class PerformanceContext(
    val play: Play,
    val audience: Int,
    val amount: Int,
    val volumeCredits: Int,
)

data class Invoice(
    val customer: String,
    val performances: List<Performance>
)

data class StatementData(
    val customer: String,
    val performances: List<PerformanceContext>,
    val totalAmount: Int,
    val totalVolumeCredits: Int,
)
