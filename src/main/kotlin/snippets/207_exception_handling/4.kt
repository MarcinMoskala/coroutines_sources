package f_207_exception_handling.s_4

import kotlinx.coroutines.*

//sampleStart
// DON'T DO THAT!
fun main(): Unit = runBlocking(SupervisorJob()) {
    launch { // 1
        delay(1000)
        throw Error("Some error")
    }
    launch { // 2
        delay(2000)
        println("Will not be printed")
    }
}
// Exception...
//sampleEnd
