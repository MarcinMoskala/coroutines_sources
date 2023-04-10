package f_101_why.s_2

import kotlinx.coroutines.*

fun main() = runBlocking {
  repeat(100_000) {
      launch {
          delay(1000L)
          print(".")
      }
  }
}
