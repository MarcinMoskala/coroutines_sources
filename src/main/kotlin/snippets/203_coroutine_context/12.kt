package f_203_coroutine_context.s_12

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

suspend fun printName() {
    println(coroutineContext[CoroutineName]?.name)
}

suspend fun main() { 
    printName() // null
    a()
}

suspend fun a() = withContext(CoroutineName("a")) {
    printName() // a
    b()
}

suspend fun b() = withContext(CoroutineName("b")) {
    printName() // b
}
