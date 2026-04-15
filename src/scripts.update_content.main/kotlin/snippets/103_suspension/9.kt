package f_103_suspension.s_9

import kotlinx.coroutines.*
import kotlin.coroutines.resume

suspend fun main() {
    val i: Int = suspendCancellableCoroutine<Int> { c ->
        c.resume(42)
    }
    println(i) // 42

    val str: String = suspendCancellableCoroutine<String> { c ->
        c.resume("Some text")
    }
    println(str) // Some text

    val b: Boolean = suspendCancellableCoroutine<Boolean> { c ->
        c.resume(true)
    }
    println(b) // true
}
