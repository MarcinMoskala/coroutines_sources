package f_201_starting_coroutines.s_2

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//sampleStart
suspend fun main() {
    val job1 = GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    val job2 = GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    val job3 = GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    job1.join()
    job2.join()
    job3.join()
}
// Hello,
// (1 sec)
// World!
// World!
// World!
//sampleEnd
