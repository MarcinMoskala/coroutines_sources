package f_307_flow_lifecycle.s_13

import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.flow

suspend fun main() {
    flow {
        emit(1)
        emit(2)
        error("E")
        emit(3)
    }.retry(3) {
        print(it.message)
        true
    }.collect { print(it) } // 12E12E12E12(exception thrown)
}
