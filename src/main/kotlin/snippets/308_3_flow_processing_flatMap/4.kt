package f_308_3_flow_processing_flatMap.s_4

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

fun flowFrom(elem: String) = flowOf(1, 2, 3)
    .onEach { delay(1000) }
    .map { "${it}_${elem} " }

suspend fun main() {
    flowOf("A", "B", "C")
        .flatMapLatest { flowFrom(it) }
        .collect { println(it) }
}
// (1 sec)
// 1_C
// (1 sec)
// 2_C
// (1 sec)
// 3_C
