package f_305_understanding_flow.s_10

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun main() {
    flowOf("A", "B", "C")
        .onEach { delay(1000) }
        .collect { println(it) }
}
