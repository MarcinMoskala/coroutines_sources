package f_206_cancellation.s_6

import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            println("Coroutine started")
            delay(200)
            println("Coroutine finished")
        } finally {
            println("Finally")
            launch {
                println("Children executed")
            }
            delay(1000L)
            println("Cleanup done")
        }
    }
    delay(100)
    job.cancelAndJoin()
    println("Done")
}

