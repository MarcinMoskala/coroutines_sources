package f_204_cancellation.s_3

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//sampleStart
suspend fun main() = coroutineScope {
  val job = launch {
      repeat(1_000) { i ->
          delay(100)
          Thread.sleep(100) // We simulate long operation
          println("Printing $i")
      }
  }

  delay(1000)
  job.cancel()
  job.join()
  println("Cancelled successfully")
}
// Printing 0
// Printing 1
// Printing 2
// Printing 3
// Printing 4
// Cancelled successfully
//sampleEnd
