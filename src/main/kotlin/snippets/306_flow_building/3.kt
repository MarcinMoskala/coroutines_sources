package f_306_flow_building.s_3

import kotlinx.coroutines.flow.*

suspend fun main() {
    listOf(1, 2, 3, 4, 5)
        // or setOf(1, 2, 3, 4, 5)
        // or sequenceOf(1, 2, 3, 4, 5)
        .asFlow()
        .collect { print(it) } // 12345
}
