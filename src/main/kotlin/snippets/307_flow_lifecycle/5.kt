package f_307_flow_lifecycle.s_5

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

suspend fun main() = coroutineScope {
    flowOf(1, 2)
        .onEach { delay(1000) }
        .onCompletion { println("Completed") }
        .collect { println(it) }
}
// (1 sec)
// 1
// (1 sec)
// 2
// Completed
