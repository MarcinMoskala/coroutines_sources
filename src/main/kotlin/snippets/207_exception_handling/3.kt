package f_207_exception_handling.s_3

import kotlinx.coroutines.*

//sampleStart
fun main(): Unit = runBlocking {
  val scope = CoroutineScope(SupervisorJob())
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
// Exception...
// Will be printed
//sampleEnd
