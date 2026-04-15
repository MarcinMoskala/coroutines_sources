package f_203_coroutine_context.s_11

import kotlinx.coroutines.*

suspend fun printName() = coroutineScope {
    println(coroutineContext[CoroutineName]?.name)
}

fun main() = runBlocking(CoroutineName("Outer")) {
    printName() // Outer
    launch(CoroutineName("Inner")) {
        printName() // Inner
    }
    delay(10)
    printName() // Outer
}
