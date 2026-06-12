package f_308_5_debounce_sample.s_3

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

suspend fun main() {
    flow {
        emit(1)
        delay(400)
        emit(2)
        delay(600)
        emit(3)
        delay(1000)
        emit(4)
        delay(1000)
        emit(5)
    }
        .debounce(800)
        .collect { println(it) }
}
