package f_103_suspension.s_13

import kotlinx.coroutines.*
import kotlin.coroutines.*

var continuation: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation() {
    suspendCancellableCoroutine<Unit> { cont ->
        continuation = cont
    }
}

suspend fun main() {
    println("Before")

    suspendAndSetContinuation()
    continuation?.resume(Unit)

    println("After")
}
