package f_103_suspension.s_3

import kotlinx.coroutines.*
import kotlin.coroutines.resume

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        println("Before too")
        continuation.resume(Unit)
    }

    println("After")
}
