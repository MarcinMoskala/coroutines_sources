package f_201_builders.s_12

import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
   launch {
       delay(1000L)
       println("World!")
   }
   println("Hello,")
}
// Hello,
// (1 sec)
// World!
//sampleEnd
