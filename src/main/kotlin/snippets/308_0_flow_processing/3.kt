package f_308_0_flow_processing.s_3

import kotlinx.coroutines.flow.*

suspend fun main() {
    ('A'..'Z').asFlow()
        .take(5) // [A, B, C, D, E]
        .collect { print(it) } // ABCDE
}
