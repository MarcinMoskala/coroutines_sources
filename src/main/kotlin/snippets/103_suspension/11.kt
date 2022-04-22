package f_103_suspension.s_11

import kotlin.coroutines.*

//sampleStart
class MyException : Throwable("Just an exception")

suspend fun main() {
   try {
       suspendCoroutine<Unit> { cont ->
           cont.resumeWithException(MyException())
       }
   } catch (e: MyException) {
       println("Caught!")
   }
}
// Caught!
//sampleEnd
