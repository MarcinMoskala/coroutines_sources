package f_101_why.s_1

import kotlin.concurrent.thread

fun main() {
  repeat(100_000) {
      thread {
          Thread.sleep(1000L)
          print(".")
      }
  }
}
