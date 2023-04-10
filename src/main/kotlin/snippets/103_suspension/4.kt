package f_103_suspension.s_4

import kotlin.coroutines.*

//sampleStart
suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        continuation.resume(Unit)
    }

    println("After")
}
// Before
// After
//sampleEnd
