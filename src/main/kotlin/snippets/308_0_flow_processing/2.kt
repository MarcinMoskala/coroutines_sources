package f_308_0_flow_processing.s_2

import kotlinx.coroutines.flow.*

suspend fun main() {
    (1..10).asFlow() // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        .filter { it <= 5 } // [1, 2, 3, 4, 5]
        .filter { isEven(it) } // [2, 4]
        .collect { print(it) } // 24
}

fun isEven(num: Int): Boolean = num % 2 == 0
