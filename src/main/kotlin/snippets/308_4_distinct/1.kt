package f_308_4_distinct.s_1

import kotlinx.coroutines.flow.*

suspend fun main() {
    flowOf(1, 2, 2, 3, 2, 1, 1, 3)
        .distinctUntilChanged()
        .collect { print(it) } // 123213
}
