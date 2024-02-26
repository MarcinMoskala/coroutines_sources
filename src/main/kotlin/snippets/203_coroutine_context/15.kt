package f_203_coroutine_context.s_15

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class MyId(val id: String) : AbstractCoroutineContextElement(MyId) {
    companion object Key : CoroutineContext.Key<MyId>
}

suspend fun getId() = coroutineContext[MyId]?.id

suspend fun main() = withContext(Dispatchers.Default) {
    val id = MyId("A")
    println(getId()) // null
    withContext(id) {
        println(getId()) // A
        withContext(Dispatchers.IO) {
            println(getId()) // A
        }
    }
    println(getId()) // null
}
