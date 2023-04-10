package f_103_suspension.s_14

import kotlinx.coroutines.*
import kotlin.coroutines.*

//sampleStart
// Do not do this, potential memory leak
var continuation: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation() {
   suspendCoroutine<Unit> { cont ->
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
// Before
// (1 second delay)
// After
//sampleEnd
