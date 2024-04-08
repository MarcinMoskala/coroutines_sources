package f_103_suspension.s_5

import kotlin.concurrent.thread
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
