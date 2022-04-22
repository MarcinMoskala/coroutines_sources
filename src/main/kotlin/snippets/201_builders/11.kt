package f_201_builders.s_11

import kotlinx.coroutines.*

//sampleStart
fun main() = runBlocking {
   this.launch { // same as just launch
       delay(1000L)
       println("World!")
   }
   launch { // same as this.launch
       delay(2000L)
       println("World!")
   }
   println("Hello,")
}
// Hello,
// (1 sec)
// World!
// (1 sec)
// World!
//sampleEnd
