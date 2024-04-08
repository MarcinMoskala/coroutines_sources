package f_201_starting_coroutines.s_11

import kotlinx.coroutines.*

suspend fun longTask() = coroutineScope {
    launch {
        delay(1000)
        println("Finished task 1")
    }
    launch {
        delay(2000)
        println("Finished task 2")
    }
}

suspend fun main() {
    println("Before")
    longTask()
    println("After")
}
