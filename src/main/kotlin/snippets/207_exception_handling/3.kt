package f_207_exception_handling.s_3

import kotlinx.coroutines.*

//sampleStart
fun main(): Unit = runBlocking {
    // DON'T DO THAT!
    launch(SupervisorJob()) { // 1
        launch {
            delay(1000)
            throw Error("Some error")
        }

        launch {
            delay(2000)
            println("Will not be printed")
        }
    }

    delay(3000)
}
// Exception...
//sampleEnd
