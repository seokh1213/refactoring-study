package monster.monkeyking.chapter01

typealias Plays = Map<String, Play>

enum class PlayType {
    TRAGEDY,
    COMEDY
}

data class Play(
    val name: String,
    val type: PlayType
)

data class Performance(
    val playID: String,
    val audience: Int
)

data class EnrichedPerformance(
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
    val performances: List<EnrichedPerformance>,
    val totalAmount: Int,
    val totalVolumeCredits: Int,
)
