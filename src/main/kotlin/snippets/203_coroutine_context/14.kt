package f_203_coroutine_context.s_14

import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class CoroutineName(val name: String) : ThreadContextElement<String> {
    companion object Key : CoroutineContext.Key<CoroutineName>

    override val key: CoroutineContext.Key<CoroutineName> = Key

    // this is invoked before coroutine is resumed on current thread
    override fun updateThreadContext(context: CoroutineContext): String {
        val previousName = Thread.currentThread().name
        Thread.currentThread().name = "$previousName # $name"
        return previousName
    }

    // this is invoked after coroutine has suspended on current thread
    override fun restoreThreadContext(context: CoroutineContext, oldState: String) {
        Thread.currentThread().name = oldState
    }
}

fun main() = runBlocking(CoroutineName("MyCoroutine")) {
    println("Running in thread: ${Thread.currentThread().name}")
}
