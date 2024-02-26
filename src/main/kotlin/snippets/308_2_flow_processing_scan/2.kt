package f_308_2_flow_processing_scan.s_2

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

suspend fun main() {
    val list = flowOf(1, 2, 3, 4)
        .onEach { delay(1000) }
    val res = list.fold(0) { acc, i -> acc + i }
    println(res)
}
// (4 sec)
// 10
