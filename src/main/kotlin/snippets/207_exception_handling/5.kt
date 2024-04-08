package f_207_exception_handling.s_5

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    supervisorScope {
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
    println("Done")
}
