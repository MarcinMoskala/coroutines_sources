package f_204_dispatchers.s_9

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun main() = measureTimeMillis {
    val dispatcher = Dispatchers.IO
        .limitedParallelism(100_000)
    coroutineScope {
        repeat(100_000) {
            launch(dispatcher) {
                Thread.sleep(1000)
            }
        }
    }
}.let(::println) // 23 803
