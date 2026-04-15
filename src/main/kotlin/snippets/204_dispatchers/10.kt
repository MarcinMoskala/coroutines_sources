package f_204_dispatchers.s_10

import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

fun main() {
    var continuation: Continuation<Unit>? = null

    thread(name = "Thread1") {
        runBlocking(Dispatchers.Unconfined) {
            println(Thread.currentThread().name) // Thread1

            suspendCancellableCoroutine {
                continuation = it
            }

            println(Thread.currentThread().name) // Thread2
            
            delay(1000)
            
            println(Thread.currentThread().name) 
            // kotlinx.coroutines.DefaultExecutor (used by delay)
        }
    }

    thread(name = "Thread2") {
        Thread.sleep(1000)
        continuation?.resume(Unit)
    }
}
