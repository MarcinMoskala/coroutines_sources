package f_201_starting_coroutines.s_5

import kotlinx.coroutines.*

//sampleStart
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
// Hello,
// (1 sec)
// World!
// World!
// World!
//sampleEnd
