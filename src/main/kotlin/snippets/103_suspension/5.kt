package f_103_suspension.s_5

import kotlin.concurrent.thread
import kotlin.coroutines.*

//sampleStart
suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        thread {
            println("Suspended")
            Thread.sleep(1000)
            continuation.resume(Unit)
            println("Resumed")
        }
    }

    println("After")
}
// Before
// Suspended
// (1 second delay)
// After
// Resumed
//sampleEnd
