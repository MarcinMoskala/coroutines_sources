package f_208_XXX_coroutine_scope_functions.s_6

import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    launch {
        delay(1000)
        println("World")
    }
    println("Hello, ")
}
// Hello
// (1 sec)
// World
//sampleEnd
