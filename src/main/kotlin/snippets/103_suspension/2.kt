package f_103_suspension.s_2

import kotlin.coroutines.*

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { }

    println("After")
}
