package f_308_5_debounce_sample.s_4

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample

suspend fun main() {
    flow {
        repeat(9) { // Only the last element will be received
            delay(100)
            emit(it)
        }
        delay(1000)
        emit(10) // This element will be received
        delay(200)
        emit(11) // This element will be received
        delay(1000)
        emit(12) // This element will be ignored 
        // due to flow completion
    }.sample(1000)
        .collect { println(it) }
}
