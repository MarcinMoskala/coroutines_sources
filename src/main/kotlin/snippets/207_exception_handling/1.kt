package f_207_exception_handling.s_1

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    launch {
        launch {
            delay(1000)
            throw Error("Some error")
        }
        launch {
            delay(2000)
            println("Will not be printed")
        }
        launch {
            delay(500) // faster than the exception
            println("Will be printed")
        }
    }
    launch {
        delay(2000)
        println("Will not be printed")
    }
}
