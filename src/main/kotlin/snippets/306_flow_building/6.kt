package f_306_flow_building.s_6

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

fun makeFlow(): Flow<Int> = flow {
    repeat(3) { num ->
        delay(1000)
        emit(num)
    }
}

suspend fun main() {
    makeFlow()
        .collect { println(it) }
}
// (1 sec)
// 0
// (1 sec)
// 1
// (1 sec)
// 2
