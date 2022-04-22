package f_307_flow_lifecycle.s_4

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

suspend fun main() {
    flowOf(1, 2)
        .onEach { delay(1000) }
        .onStart { emit(0) }
        .collect { println(it) }
}
// 0
// (1 sec)
// 1
// (1 sec)
// 2
