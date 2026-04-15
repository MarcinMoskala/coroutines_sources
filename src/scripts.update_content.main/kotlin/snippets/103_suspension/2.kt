package f_103_suspension.s_2

import kotlinx.coroutines.*

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { }

    println("After")
}
