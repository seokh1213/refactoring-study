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
<h1>청구 내역 (고객명: BigCo)</h1>
<table>
  <tr>
      <th>연극</th>
      <th>좌석수</th>
      <th>금액</th>
  </tr>
  <tr>
      <td>Hamlet</td>
      <td>55석</td>
      <td>$650.00</td>
  </tr>
  <tr>
      <td>As You Like It</td>
      <td>35석</td>
      <td>$580.00</td>
  </tr>
  <tr>
      <td>Othello</td>
      <td>40석</td>
      <td>$500.00</td>
  </tr>
</table>
<p>총액: <em>$1730.00</em></p>
<p>적립 포인트: <em>47</em> 점</p>
            """.trimIndent(),
            statement(invoice, plays).trimIndent()
        )
    }

}
