# Chapter 01 - 리팩터링: 첫번째 예시

## 원본 코드
```kotlin
data class Plays(
    val plays: Map<String, Play>
)

data class Play(
    val name: String,
    val type: String
)

data class Performance(
    val playID: String,
    val audience: Int
)

data class Invoice(
    val customer: String,
    val performances: List<Performance>
)

fun statement(invoice: Invoice, plays: Plays ): String {
    var totalAmount = 0
    var volumeCredits = 0
    var result = "청구 내역 (고객명: ${invoice.customer})\n"
    val format = { amount: Int -> "$${amount / 100}.00" }

    for (perf in invoice.performances) {
        val play = plays.plays[perf.playID] ?: error("알 수 없는 장르: ${perf.playID}")
        var thisAmount = 0

        when (play.type) {
            "tragedy" -> {
                thisAmount = 40000
                if (perf.audience > 30) {
                    thisAmount += 1000 * (perf.audience - 30)
                }
            }
            "comedy" -> {
                thisAmount = 30000
                if (perf.audience > 20) {
                    thisAmount += 10000 + 500 * (perf.audience - 20)
                }
                thisAmount += 300 * perf.audience
            }
            else -> error("알 수 없는 장르: ${play.type}")
        }

        // 포인트를 적립한다.
        volumeCredits += maxOf(perf.audience - 30, 0)
        // 희극 관객 5명마다 추가 포인트를 적립한다.
        if ("comedy" == play.type) volumeCredits += perf.audience / 5

        // 청구 내역을 출력한다.
        result += "  ${play.name}: ${format(thisAmount)} (${perf.audience}석)\n"
        totalAmount += thisAmount
    }

    result += "총액: ${format(totalAmount)}\n"
    result += "적립 포인트: $volumeCredits 점\n"
    return result
}

fun main() {
    val plays = Plays(
        mapOf(
            "hamlet" to Play("Hamlet", "tragedy"),
            "as-like" to Play("As You Like It", "comedy"),
            "othello" to Play("Othello", "tragedy")
        )
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
}
```

## 자 시작해보자

> 프로그램이 새로운 기능을 추가하기에 편한 구조가 아니라면 먼저 기능을 추가하기 쉬운 형태로 리팩터링하고 나서 원하는 기능을 추가하라. 


## 리팩터링의 첫 단계 ⭐️
> ⭐️ 리팩터링하기 전에 제대로 된 테스트부터 마련한다. 테스트는 반드시 자가진단하도록 만든다.

### statement() 함수 쪼개기

#### 1. when 절 분리
amountFor(perf: Performance) 함수로 추출

```kotlin
fun amountFor(perf: Performance, play: Play): Int {
    var thisAmount = 0
    when (play.type) {
        "tragedy" -> {
            thisAmount = 40000
            if (perf.audience > 30) {
                thisAmount += 1000 * (perf.audience - 30)
            }
        }
        "comedy" -> {
            thisAmount = 30000
            if (perf.audience > 20) {
                thisAmount += 10000 + 500 * (perf.audience - 20)
            }
            thisAmount += 300 * perf.audience
        }
        else -> error("알 수 없는 장르: ${play.type}")
    }
    return thisAmount
}
```

> 이렇게 수정하고 나면 곧바로 컴파일하고 테스트해서 실수한 게 없는지 확인한다. <br/>
> 리팩터링은 프로그램 수정을 작은 단계로 나눠 진행한다. 그래서 중간에 실수하더라도 버그를 쉽게 찾을 수 있다.
