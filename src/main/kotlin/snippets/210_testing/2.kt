package f_210_testing.s_2

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*

fun main() {
    val scheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(scheduler)

    CoroutineScope(testDispatcher).launch {
        println("Some work 1")
        delay(1000)
        println("Some work 2")
        delay(1000)
        println("Coroutine done")
    }

    CoroutineScope(testDispatcher).launch {
        delay(500)
        println("Different work")
    }

    println("[${scheduler.currentTime}] Before")
    scheduler.advanceUntilIdle()
    println("[${scheduler.currentTime}] After")
}
