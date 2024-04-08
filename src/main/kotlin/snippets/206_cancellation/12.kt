package f_206_cancellation.s_12

import kotlinx.coroutines.*

class MyNonPropagatingException : CancellationException()

suspend fun main(): Unit = coroutineScope {
  launch { // 1
      launch { // 2
          delay(2000)
          println("Will not be printed")
      }
      delay(1000)
      throw MyNonPropagatingException() // 3
  }
  launch { // 4
      delay(2000)
      println("Will be printed")
  }
}
