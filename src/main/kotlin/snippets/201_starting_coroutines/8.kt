package f_201_starting_coroutines.s_8

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

//sampleStart
suspend fun main() {
    println("A")
    val a: Int = coroutineScope {
        delay(1000L)
        10
    }
    println("B")
    val b: Int = coroutineScope {
        delay(1000L)
        20
    }
    println("C")
    println(a + b)
}
// A
// (1 sec)
// B
// (1 sec)
// C
// 30
//sampleEnd
