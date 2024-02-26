package f_203_coroutine_context.s_14

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.withContext

val id = ThreadLocal<String?>()

suspend fun main() = withContext(Dispatchers.Default) {
    println(id.get()) // null
    withContext(id.asContextElement("A")) {
        println(id.get()) // A
        withContext(Dispatchers.IO) {
            println(id.get()) // A
        }
    }
    println(id.get()) // null
}
