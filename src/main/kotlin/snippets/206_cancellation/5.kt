package f_206_cancellation.s_5

import kotlinx.coroutines.*

suspend fun main() {
    val scope = CoroutineScope(Job())
    scope.cancel()
    val job = scope.launch { // will be ignored
        println("Will not be printed")
    }
    job.join()
}
