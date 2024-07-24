package f_103_suspension.s_14

import kotlinx.coroutines.*
import kotlin.coroutines.*

var continuation: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation() {
    suspendCancellableCoroutine<Unit> { cont ->
       continuation = cont
   }
}

suspend fun main() = coroutineScope {
   println("Before")

   launch {
       delay(1000)
       continuation?.resume(Unit)
   }

   suspendAndSetContinuation()
   println("After")
}
