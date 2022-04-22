package f_204_cancellation.s_5

import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
   val job = Job()
   launch(job) {
       try {
           repeat(1_000) { i ->
               delay(200)
               println("Printing $i")
           }
       } catch (e: CancellationException) {
           println(e)
           throw e
       }
   }
   delay(1100)
   job.cancelAndJoin()
   println("Cancelled successfully")
   delay(1000)
}
// Printing 0
// Printing 1
// Printing 2
// Printing 3
// Printing 4
// JobCancellationException...
// Cancelled successfully
//sampleEnd
