package f_205_exception_handling.s_2

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//sampleStart
fun main(): Unit = runBlocking {
   // Don't wrap in a try-catch here. It will be ignored.
   try {
       launch {
           delay(1000)
           throw Error("Some error")
       }
   } catch (e: Throwable) { // nope, does not help here
       println("Will not be printed")
   }

   launch {
       delay(2000)
       println("Will not be printed")
   }
}
// Exception in thread "main" java.lang.Error: Some error...
//sampleEnd
