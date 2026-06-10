package f_204_dispatchers.s_8

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun main(): Unit = coroutineScope {
    val dispatcher = Dispatchers.Default
        .limitedParallelism(1)

    val launch = launch(dispatcher) {
        repeat(5) {
            launch {
                Thread.sleep(1000)
            }
        }
    }
    val time = measureTimeMillis { launch.join() }
    println("Took $time") // Took 5006
}
