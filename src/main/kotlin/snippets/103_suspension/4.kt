package f_103_suspension.s_4

import kotlin.concurrent.thread
import kotlinx.coroutines.*
import kotlin.coroutines.resume

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        thread {
            println("Suspended")
            Thread.sleep(1000)
            continuation.resume(Unit)
            println("Resumed")
        }
    }

    println("After")
}
