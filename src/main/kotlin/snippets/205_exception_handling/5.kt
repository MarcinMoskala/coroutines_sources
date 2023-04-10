package f_205_exception_handling.s_5

import kotlinx.coroutines.*

//sampleStart
fun main(): Unit = runBlocking {
  val job = SupervisorJob()
  launch(job) {
      delay(1000)
      throw Error("Some error")
  }
  launch(job) {
      delay(2000)
      println("Will be printed")
  }
  job.join()
}
// (1 sec)
// Exception...
// (1 sec)
// Will be printed
//sampleEnd
