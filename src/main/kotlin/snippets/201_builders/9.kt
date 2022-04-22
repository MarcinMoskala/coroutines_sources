package f_201_builders.s_9

import kotlinx.coroutines.*

//sampleStart
fun main() = runBlocking {
   // Don't do that!
   // this is misleading to use async as launch
   GlobalScope.async {
       delay(1000L)
       println("World!")
   }
   println("Hello,")
   delay(2000L)
}
// Hello,
// (1 sec)
// World!
//sampleEnd
