package f_205_job.s_1

import kotlinx.coroutines.*

//sampleStart
fun main(): Unit = runBlocking {
  val job: Job = launch {
      delay(1000)
      println("Test")
  }
}
//sampleEnd
