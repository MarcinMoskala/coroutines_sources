package f_103_suspension.s_7

import java.util.concurrent.*
import kotlin.coroutines.*

private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun delay(timeMillis: Long): Unit =
    suspendCancellableCoroutine { cont ->
        executor.schedule({
            cont.resume(Unit)
        }, timeMillis, TimeUnit.MILLISECONDS)
    }

suspend fun main() {
    println("Before")

    delay(1000)

    println("After")
}
