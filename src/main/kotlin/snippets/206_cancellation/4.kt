package f_206_cancellation.s_4

import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    var childJob: Job? = null
    val job = launch {
        launch {
            try {
                delay(1000)
                println("A")
            } finally {
                println("A finished")
            }
        }
        childJob = launch {
            try {
                delay(2000)
                println("B")
            } catch (e: CancellationException) {
                println("B cancelled")
            }
        }
        launch {
            delay(3000)
            println("C")
        }.invokeOnCompletion {
            println("C finished")
        }
    }
    delay(100)
    job.cancel()
    job.join()
    println("Cancelled successfully")
    println(childJob?.isCancelled)
}
// (0.1 sec)
// (the below order might be different)
// A finished
// B cancelled
// C finished
// Cancelled successfully
// true
