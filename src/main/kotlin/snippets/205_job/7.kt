package f_205_job.s_7

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
  launch(Job()) { // the new job replaces one from parent
      delay(1000)
      println("Will not be printed")
  }
}
// (prints nothing, finishes immediately)
