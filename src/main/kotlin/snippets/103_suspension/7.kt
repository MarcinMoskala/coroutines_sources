package f_103_suspension.s_7

import java.util.concurrent.*
import kotlin.coroutines.*

//sampleStart
private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        executor.schedule({
            continuation.resume(Unit)
        }, 1000, TimeUnit.MILLISECONDS)
    }

    println("After")
}
// Before
// (1 second delay)
// After
//sampleEnd
