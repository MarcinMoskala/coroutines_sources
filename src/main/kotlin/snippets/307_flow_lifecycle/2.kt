package f_307_flow_lifecycle.s_2

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

suspend fun main() {
    flowOf(1, 2)
        .onEach { delay(1000) }
        .collect { println(it) }
}
// (1 sec)
// 1
// (1 sec)
// 2
