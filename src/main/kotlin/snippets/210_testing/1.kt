package f_210_testing.s_1

import kotlinx.coroutines.test.TestCoroutineScheduler

fun main() {
    val scheduler = TestCoroutineScheduler()

    println(scheduler.currentTime) // 0
    scheduler.advanceTimeBy(1_000)
    println(scheduler.currentTime) // 1000
    scheduler.advanceTimeBy(1_000)
    println(scheduler.currentTime) // 2000
}
