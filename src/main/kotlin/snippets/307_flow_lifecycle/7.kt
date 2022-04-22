package f_307_flow_lifecycle.s_7

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

suspend fun main() = coroutineScope {
    flow<List<Int>> { delay(1000) }
        .onEmpty { emit(emptyList()) }
        .collect { println(it) }
}
// (1 sec)
// []
