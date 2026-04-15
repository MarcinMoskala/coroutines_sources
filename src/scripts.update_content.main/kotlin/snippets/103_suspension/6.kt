package f_103_suspension.s_6

import kotlin.concurrent.thread
import kotlinx.coroutines.*
import kotlin.coroutines.*

fun continueAfterSecond(continuation: Continuation<Unit>) {
    thread {
        Thread.sleep(1000)
        continuation.resume(Unit)
    }
}

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        continueAfterSecond(continuation)
    }

    println("After")
}
