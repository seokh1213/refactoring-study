# Chapter 01 - 리팩터링: 첫번째 예시

## 요약
1. 테스트 코드 작성

2. 기능 파악을 위해, 큰 덩어리를 함수로 작게 추출
   * 변수 인라인 하기
   * 변수명 역할을 포함하여 변경
   * 반복문 리팩터링
      * 반복문 책임에 따라 분리하기
         * 반복문을 여러번에 나눠서 실행하면 성능 저하가 발생할거라 생각하지만, 실제로는 미비하고 유지보수의 장점이 더 커진다.
      * 지역 변수 선언 위치 옮기기
      * 함수로 분리
      * 변수 인라인하기

3. 기능 개선
   * 단계 쪼개기 (책임 분리하기)
   * 반복문을 파이프라인으로 바꾸기 (함수형 프로그래밍)
   * 조건부 로직을 다형성으로 바꾸기

## 최초 코드

```kotlin
typealias Plays = Map<String, Play>

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

fun statement(invoice: Invoice, plays: Plays): String {
    var totalAmount = 0
    var volumeCredits = 0
    var result = "청구 내역 (고객명: ${invoice.customer})\n"
    val format = { amount: Int -> "$${amount / 100}.00" }

    for (perf in invoice.performances) {
        val play = plays[perf.playID] ?: error("알 수 없는 장르: ${perf.playID}")
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
    val plays = mapOf(
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

> 리팩터링은 프로그램 수정을 작은 단계로 나눠 진행한다. 그래서 중간에 실수하더라도 버그를 쉽게 찾을 수 있다.
>
> "이렇게 수정하고 나면 곧바로 컴파일하고 테스트해서 실수한 게 없는지 확인한다. 문제가 없으면 커밋 한다."

#### 2. 변수명 변경

- thisAmount -> result로 변경

```kotlin
fun amountFor(perf: Performance, play: Play): Int {
    var result = 0
    when (play.type) {
        "tragedy" -> {
            result = 40000
            if (perf.audience > 30) {
                result += 1000 * (perf.audience - 30)
            }
        }
        "comedy" -> {
            result = 30000
            if (perf.audience > 20) {
                result += 10000 + 500 * (perf.audience - 20)
            }
            result += 300 * perf.audience
        }
        else -> error("알 수 없는 장르: ${play.type}")
    }
    return result
}
```

- perf -> aPerformance로 변경

```kotlin
fun amountFor(aPerformance: Performance, play: Play): Int {
    var result = 0
    when (play.type) {
        "tragedy" -> {
            result = 40000
            if (aPerformance.audience > 30) {
                result += 1000 * (aPerformance.audience - 30)
            }
        }
        "comedy" -> {
            result = 30000
            if (aPerformance.audience > 20) {
                result += 10000 + 500 * (aPerformance.audience - 20)
            }
            result += 300 * aPerformance.audience
        }
        else -> error("알 수 없는 장르: ${play.type}")
    }
    return result
}
```

- play 조회 함수 분리

```kotlin
fun playFor(aPerformance: Performance): Play {
    return plays[aPerformance.playID] ?: error("알 수 없는 장르: ${aPerformance.playID}")
}

val play = playFor(perf)
val thisAmount = amountFor(perf, play)
```

- 변수 인라인하기 (책에서는 좀 더 과격하게 도입. README에는 생략)

```kotlin
val thisAmount = amountFor(perf, playFor(perf))
```

- 중간 결과

```kotlin
typealias Plays = Map<String, Play>

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

fun statement(invoice: Invoice, plays: Plays): String {
    var totalAmount = 0
    var volumeCredits = 0
    var result = "청구 내역 (고객명: ${invoice.customer})\n"
    val format = { amount: Int -> "$${amount / 100}.00" }

    fun playFor(aPerformance: Performance): Play {
        return plays[aPerformance.playID] ?: error("알 수 없는 장르: ${aPerformance.playID}")
    }

    fun amountFor(aPerformance: Performance): Int {
        var result = 0
        when (playFor(aPerformance).type) {
            "tragedy" -> {
                result = 40000
                if (aPerformance.audience > 30) {
                    result += 1000 * (aPerformance.audience - 30)
                }
            }

            "comedy" -> {
                result = 30000
                if (aPerformance.audience > 20) {
                    result += 10000 + 500 * (aPerformance.audience - 20)
                }
                result += 300 * aPerformance.audience
            }

            else -> error("알 수 없는 장르: ${playFor(aPerformance).type}")
        }
        return result
    }

    for (perf in invoice.performances) {
        // 포인트를 적립한다.
        volumeCredits += maxOf(perf.audience - 30, 0)
        // 희극 관객 5명마다 추가 포인트를 적립한다.
        if ("comedy" == playFor(perf).type) volumeCredits += perf.audience / 5

        // 청구 내역을 출력한다.
        result += "  ${playFor(perf).name}: ${format(amountFor(perf))} (${perf.audience}석)\n"
        totalAmount += amountFor(perf)
    }

    result += "총액: ${format(totalAmount)}\n"
    result += "적립 포인트: $volumeCredits 점\n"
    return result
}
```

#### 3. 반복문 리팩터링
1. 반복문 책임에 따라 분리하기
    - 반복문을 여러번에 나눠서 실행하면 성능 저하가 발생할거라 생각하지만, 실제로는 미비하고 유지보수의 장점이 더 커진다.
2. 지역 변수 선언 위치 옮기기
3. 함수로 분리
4. 변수 인라인하기

## 중간 결과
```kotlin

typealias Plays = Map<String, Play>

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

fun statement(invoice: Invoice, plays: Plays): String {
    fun usd(aNumber: Int): String {
        return "$${aNumber / 100}.00"
    }

    fun playFor(aPerformance: Performance): Play {
        return plays[aPerformance.playID] ?: error("알 수 없는 장르: ${aPerformance.playID}")
    }

    fun amountFor(aPerformance: Performance): Int {
        var result = 0
        when (playFor(aPerformance).type) {
            "tragedy" -> {
                result = 40000
                if (aPerformance.audience > 30) {
                    result += 1000 * (aPerformance.audience - 30)
                }
            }

            "comedy" -> {
                result = 30000
                if (aPerformance.audience > 20) {
                    result += 10000 + 500 * (aPerformance.audience - 20)
                }
                result += 300 * aPerformance.audience
            }

            else -> error("알 수 없는 장르: ${playFor(aPerformance).type}")
        }
        return result
    }

    fun volumeCreditsFor(aPerformance: Performance): Int {
        var result = 0
        result += maxOf(aPerformance.audience - 30, 0)
        if ("comedy" == playFor(aPerformance).type) result += aPerformance.audience / 5
        return result
    }

    fun totalAmount(): Int {
        var totalAmount = 0
        for (perf in invoice.performances) {
            totalAmount += amountFor(perf)
        }
        return totalAmount
    }

    fun totalVolumeCredits(): Int {
        var result = 0
        for (perf in invoice.performances) {
            // 포인트를 적립한다.
            result += volumeCreditsFor(perf)
        }
        return result
    }

    var result = "청구 내역 (고객명: ${invoice.customer})\n"
    for (perf in invoice.performances) {
        // 청구 내역을 출력한다.
        result += "  ${playFor(perf).name}: ${usd(amountFor(perf))} (${perf.audience}석)\n"
    }
    result += "총액: ${usd(totalAmount())}\n"
    result += "적립 포인트: ${totalVolumeCredits()} 점\n"
    return result
}
```
- 중첩 함수 난무 하지만 statement의 하고자 하는 흐름을 보기는 쉬워졌다.

## 기능 개선
결과 출력을 단순 문자열에서 HTML 형태로 출력하도록 변경

### 단계 쪼개기 (책임 분리하기)
데이터 처리 -> HTML 출력

### 반복문을 파이프라인으로 바꾸기 (함수형 프로그래밍)

### 조건부 로직을 다형성으로 바꾸기

