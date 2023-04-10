package f_103_suspension.s_3

import kotlin.coroutines.*

//sampleStart
suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        println("Before too")
    }

    println("After")
}
// Before
// Before too
//sampleEnd
