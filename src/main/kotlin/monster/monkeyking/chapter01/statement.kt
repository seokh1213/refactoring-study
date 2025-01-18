package monster.monkeyking.chapter01

fun statement(invoice: Invoice, plays: Plays): String {
    val statementData = createStatementData(plays, invoice)
    return renderHtml(statementData)
}

private fun usd(aNumber: Int): String {
    return "$${aNumber / 100}.00"
}

private fun renderHtml(data: StatementData): String {
    return StringBuilder().apply {
        append("<h1>청구 내역 (고객명: ${data.customer})</h1>\n")

        append("<table>\n")
        append("  <tr>\n")
        append("      <th>연극</th>\n")
        append("      <th>좌석수</th>\n")
        append("      <th>금액</th>\n")
        append("  </tr>\n")
        data.performances.forEach { perf ->
            append("  <tr>\n")
            append("      <td>${perf.play.name}</td>\n")
            append("      <td>${perf.audience}석</td>\n")
            append("      <td>${usd(perf.amount)}</td>\n")
            append("  </tr>\n")
        }
        append("</table>\n")

        append("<p>총액: <em>${usd(data.totalAmount)}</em></p>\n")
        append("<p>적립 포인트: <em>${data.totalVolumeCredits}</em> 점</p>")

    }.toString()
}
