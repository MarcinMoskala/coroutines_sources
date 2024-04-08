package f_103_suspension.s_11

import kotlin.coroutines.*

class MyException : Throwable("Just an exception")

suspend fun main() {
    try {
        suspendCancellableCoroutine<Unit> { cont ->
            cont.resumeWithException(MyException())
        }
    } catch (e: MyException) {
        println("Caught!")
    }
}
