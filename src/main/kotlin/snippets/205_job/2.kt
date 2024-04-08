package f_205_job.s_2

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
  val job: Job = launch {
      delay(1000)
      println("Test")
  }
}
