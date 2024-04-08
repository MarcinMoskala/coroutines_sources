package f_206_cancellation.s_3

import kotlinx.coroutines.*

//sampleStart
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
// (0.2 sec)
// Printing 0
// (0.2 sec)
// Printing 1
// (0.2 sec)
// Printing 2
// (0.1 sec)
// Cancelled with JobCancellationException...
// Finally
// Cancelled successfully
//sampleEnd
