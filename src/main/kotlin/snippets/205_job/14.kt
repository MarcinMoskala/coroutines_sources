package f_205_job.s_14

import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
  val parentJob = Job()
  val job = Job(parentJob)
  launch(job) {
      delay(1000)
      println("Text 1")
  }
  launch(job) {
      delay(2000)
      println("Text 2")
  }
  delay(1100)
  parentJob.cancel()
  job.children.forEach { it.join() }
}
