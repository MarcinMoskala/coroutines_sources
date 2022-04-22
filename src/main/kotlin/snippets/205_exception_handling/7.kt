package f_205_exception_handling.s_7

import kotlinx.coroutines.*

//sampleStart
class MyException : Throwable()

suspend fun main() = supervisorScope {
   val str1 = async<String> {
       delay(1000)
       throw MyException()
   }

   val str2 = async {
       delay(2000)
       "Text2"
   }

   try {
       println(str1.await())
   } catch (e: MyException) {
       println(e)
   }

   println(str2.await())
}
// MyException
// Text2
//sampleEnd
