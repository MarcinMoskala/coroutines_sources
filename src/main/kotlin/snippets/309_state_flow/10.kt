package f_309_state_flow.s_10

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*

suspend fun main(): Unit = coroutineScope {
    val state = MutableStateFlow('X')

    launch {
        for (c in 'A'..'E') {
            delay(300)
            state.value = c
            // or state.emit(c)
        }
    }

    state.collect {
        delay(1000)
        println(it)
    }
}
