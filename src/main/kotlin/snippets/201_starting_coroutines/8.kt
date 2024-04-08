package f_201_starting_coroutines.s_8

import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope

suspend fun main() {
    coroutineScope {
        delay(1000L)
        println("World!")
    }
    coroutineScope {
        delay(1000L)
        println("World!")
    }
    coroutineScope {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}
