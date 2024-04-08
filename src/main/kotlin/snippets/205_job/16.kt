package f_205_job.s_16

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val deferred = CompletableDeferred<String>()
    launch {
        println("Starting first")
        delay(1000)
        deferred.complete("Test")
        delay(1000)
        println("First done")
    }
    launch {
        println("Starting second")
        println(deferred.await()) // Wait for deferred to complete
        println("Second done")
    }
}
