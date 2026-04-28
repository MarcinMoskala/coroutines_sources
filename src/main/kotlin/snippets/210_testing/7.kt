package f_210_testing.s_7

import kotlinx.coroutines.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val dispatcher = StandardTestDispatcher()

    CoroutineScope(dispatcher).launch {
        delay(1000)
        println("Coroutine done")
    }

    Thread.sleep(Random.nextLong(2000)) // Does not matter
    // how much time we wait here, it will not influence
    // the result

    val time = measureTimeMillis {
       println("[${dispatcher.scheduler.currentTime}] Before")
       dispatcher.scheduler.advanceUntilIdle()
       println("[${dispatcher.scheduler.currentTime}] After")
    }
    println("Took $time ms")
}
