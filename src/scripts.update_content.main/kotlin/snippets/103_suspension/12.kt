package f_103_suspension.s_12

import kotlinx.coroutines.*
import kotlin.coroutines.resumeWithException

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
