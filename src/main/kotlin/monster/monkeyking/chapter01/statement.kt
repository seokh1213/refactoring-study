package monster.monkeyking.chapter01

fun statement(invoice: Invoice, plays: Plays): String {
    val statementData = createStatementData(plays, invoice)
    return renderPlainText(statementData)
}

private fun renderPlainText(data: StatementData): String {
    fun usd(aNumber: Int): String {
        return "$${aNumber / 100}.00"
    }

    var result = "청구 내역 (고객명: ${data.customer})\n"
    for (perf in data.performances) {
        // 청구 내역을 출력한다.
        result += "  ${perf.play.name}: ${usd(perf.amount)} (${perf.audience}석)\n"
    }
    result += "총액: ${usd(data.totalAmount)}\n"
    result += "적립 포인트: ${data.totalVolumeCredits} 점\n"
    return result
}
