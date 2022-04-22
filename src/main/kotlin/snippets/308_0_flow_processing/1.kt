package f_308_0_flow_processing.s_1

import kotlinx.coroutines.flow.*

suspend fun main() {
    flowOf(1, 2, 3) // [1, 2, 3]
        .map { it * it } // [1, 4, 9]
        .collect { print(it) } // 149
}
