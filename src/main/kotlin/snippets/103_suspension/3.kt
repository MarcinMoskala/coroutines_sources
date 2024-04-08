package f_103_suspension.s_3

import kotlin.coroutines.*

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        println("Before too")
        continuation.resume(Unit)
    }

    println("After")
}
