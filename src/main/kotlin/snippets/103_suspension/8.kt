package f_103_suspension.s_8

import kotlin.coroutines.*

suspend fun main() {
    val i: Int = suspendCancellableCoroutine<Int> { cont ->
        cont.resume(42)
    }
    println(i) // 42

    val str: String = suspendCancellableCoroutine<String> { cont ->
        cont.resume("Some text")
    }
    println(str) // Some text

    val b: Boolean = suspendCancellableCoroutine<Boolean> { cont ->
        cont.resume(true)
    }
    println(b) // true
}
