package f_308_0_flow_processing.s_4

import kotlinx.coroutines.flow.*

suspend fun main() {
    ('A'..'Z').asFlow()
        .drop(20) // [U, V, W, X, Y, Z]
        .collect { print(it) } // UVWXYZ
}
