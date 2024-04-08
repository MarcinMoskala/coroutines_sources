package f_201_starting_coroutines.s_6

import kotlinx.coroutines.*

fun main() = runBlocking {
    launch { // same as this.launch
        delay(1000L)
        println("World!")
    }
    launch { // same as this.launch
        delay(1000L)
        println("World!")
    }
    launch { // same as this.launch
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}
