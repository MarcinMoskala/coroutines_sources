package f_205_exception_handling.s_8

import kotlinx.coroutines.*

object MyNonPropagatingException : CancellationException()

suspend fun main(): Unit = coroutineScope {
  launch { // 1
      launch { // 2
          delay(2000)
          println("Will not be printed")
      }
      throw MyNonPropagatingException // 3
  }
  launch { // 4
      delay(2000)
      println("Will be printed")
  }
}
// (2 sec)
// Will be printed
