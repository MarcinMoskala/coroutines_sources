package f_307_flow_lifecycle.s_1

import kotlinx.coroutines.flow.*

suspend fun main() {
    flowOf(1, 2, 3, 4)
        .onEach { print(it) }
        .collect() // 1234
}
