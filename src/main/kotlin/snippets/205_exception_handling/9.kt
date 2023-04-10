package f_205_exception_handling.s_9

import kotlinx.coroutines.*

//sampleStart
fun main(): Unit = runBlocking {
  val handler =
      CoroutineExceptionHandler { ctx, exception ->
          println("Caught $exception")
      }
  val scope = CoroutineScope(SupervisorJob() + handler)
  scope.launch {
      delay(1000)
      throw Error("Some error")
  }

  scope.launch {
      delay(2000)
      println("Will be printed")
  }

  delay(3000)
}
// Caught java.lang.Error: Some error
// Will be printed
//sampleEnd
