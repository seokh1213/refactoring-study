package monster.monkeyking.chapter01

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StatementTest {
    @Test
    fun test() {
        val plays: Plays = mapOf(
            "hamlet" to Play("Hamlet", "tragedy"),
            "as-like" to Play("As You Like It", "comedy"),
            "othello" to Play("Othello", "tragedy")
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

        assertEquals(
            """
                청구 내역 (고객명: BigCo)
                  Hamlet: ${'$'}650.00 (55석)
                  As You Like It: ${'$'}580.00 (35석)
                  Othello: ${'$'}500.00 (40석)
                총액: ${'$'}1730.00
                적립 포인트: 47 점
            """.trimIndent(),
            statement(invoice, plays).trimIndent()
        )
    }

}
