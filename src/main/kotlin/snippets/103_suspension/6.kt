package f_103_suspension.s_6

import java.util.concurrent.*
import kotlin.coroutines.*

private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        executor.schedule({
            continuation.resume(Unit)
        }, 1000, TimeUnit.MILLISECONDS)
    }

    println("After")
}
