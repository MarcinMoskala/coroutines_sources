package f_206_cancellation.s_3

import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = launch {
        repeat(1_000) { i ->
            delay(200)
            println("Printing $i")
        }
    }
    job.invokeOnCompletion {
        if (it is CancellationException) {
            println("Cancelled with $it")
        }
        println("Finally")
    }
    delay(700)
    job.cancel()
    job.join()
    println("Cancelled successfully")
    delay(1000)
}
