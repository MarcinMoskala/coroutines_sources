package f_103_suspension.s_2

import kotlin.coroutines.*

//sampleStart
suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { }

    println("After")
}
// Before
//sampleEnd
