package f_104_under_the_hood.s_2

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.*

//sampleStart
fun myFunction(continuation: Continuation<Unit>): Any {
    val continuation = continuation as? MyFunctionContinuation
        ?: MyFunctionContinuation(continuation)

    var counter = continuation.counter

    if (continuation.label == 0) {
        println("Before")
        counter = 0
        continuation.counter = counter
        continuation.label = 1
        if (delay(1000, continuation) == COROUTINE_SUSPENDED){
            return COROUTINE_SUSPENDED
        }
    }
    if (continuation.label == 1) {
        counter = (counter as Int) + 1
        println("Counter: $counter")
        println("After")
        return Unit
    }
    error("Impossible")
}

class MyFunctionContinuation(
    val completion: Continuation<Unit>
) : Continuation<Unit> {
    override val context: CoroutineContext
        get() = completion.context

    var result: Result<Unit>? = null
    var label = 0
    var counter = 0

    override fun resumeWith(result: Result<Unit>) {
        this.result = result
        val res = try {
            val r = myFunction(this)
            if (r == COROUTINE_SUSPENDED) return
            Result.success(r as Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
        completion.resumeWith(res)
    }
}
//sampleEnd

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true }
}

fun delay(timeMillis: Long, continuation: Continuation<Unit>): Any {
    executor.schedule({ continuation.resume(Unit) }, timeMillis, TimeUnit.MILLISECONDS)
    return COROUTINE_SUSPENDED
}

fun main() {
    val EMPTY_CONTINUATION = object : Continuation<Unit> {
        override val context: CoroutineContext = EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) {
            // This is root coroutine, we don't need anything in this example
        }
    }
    myFunction(EMPTY_CONTINUATION)
    Thread.sleep(2000)
    // Needed to prevent main() from finishing immediately.
}

private val COROUTINE_SUSPENDED = Any()
