package f_207_exception_handling.s_6

import kotlinx.coroutines.*

//sampleStart
fun main(): Unit = runBlocking {
    // DON'T DO THAT!
    withContext(SupervisorJob()) {
        launch {
            delay(1000)
            throw Error("Some error")
        }
        launch {
            delay(2000)
            println("Will be printed")
        }
        launch {
            delay(2000)
            println("Will be printed")
        }
    }
    delay(1000)
    println("Done")
}
// (1 sec)
// Exception...
//sampleEnd
