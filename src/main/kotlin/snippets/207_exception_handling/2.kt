package f_207_exception_handling.s_2

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
    println(scope.isActive)
}
// (1 sec)
// Exception...
// (2 sec)
// Will be printed
// true
//sampleEnd
