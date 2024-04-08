package f_206_cancellation.s_2

import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = launch {
        try {
            repeat(1_000) { i ->
                delay(200)
                println("Printing $i")
            }
        } catch (e: CancellationException) {
            println("Cancelled with $e")
            throw e
        } finally {
            println("Finally")
        }
    }
    delay(700)
    job.cancel()
    job.join()
    println("Cancelled successfully")
    delay(1000)
}
