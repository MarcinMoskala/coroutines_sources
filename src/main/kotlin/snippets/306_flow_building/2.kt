package f_306_flow_building.s_2

import kotlinx.coroutines.flow.*

suspend fun main() {
    emptyFlow<Int>()
        .collect { print(it) } // (nothing)
}
