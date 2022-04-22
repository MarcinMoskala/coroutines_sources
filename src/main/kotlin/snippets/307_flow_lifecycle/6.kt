package f_307_flow_lifecycle.s_6

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

suspend fun main() = coroutineScope {
    val job = launch {
        flowOf(1, 2)
            .onEach { delay(1000) }
            .onCompletion { println("Completed") }
            .collect { println(it) }
    }
    delay(1100)
    job.cancel()
}
// (1 sec)
// 1
// (0.1 sec)
// Completed
