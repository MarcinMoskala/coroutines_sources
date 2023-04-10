package f_405_best_practices.s_1

import kotlinx.coroutines.*

//sampleStart
suspend fun longTask() = coroutineScope {
    launch {
        delay(1000)
        println("Done 1")
    }
    launch {
        delay(2000)
        println("Done 2")
    }
}

suspend fun main() {
    println("Before")
    longTask()
    println("After")
}
// Before
// (1 sec)
// Done 1
// (1 sec)
// Done 2
// After
//sampleEnd
