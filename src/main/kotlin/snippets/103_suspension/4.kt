package f_103_suspension.s_4

import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun a() {
    val a = "ABC"
    suspendCancellableCoroutine { continuation ->
        // What is stored in the continuation?
        continuation.resume(Unit)
    }
    println(a)
}

suspend fun main() {
    val list = listOf(1, 2, 3)
    val text = "Some text"
    println(text)
    delay(1000)
    a()
    println(list)
}
