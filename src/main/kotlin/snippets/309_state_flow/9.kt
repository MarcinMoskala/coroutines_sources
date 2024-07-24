package f_309_state_flow.s_9

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun main() = coroutineScope {
    val state = MutableStateFlow("A")
    
    state.onEach { println("Updated to $it") }
        .stateIn(this) // Updated to A
    
    state.value = "B" // Updated to B
    state.value = "B" // (nothing printed)
    state.emit("B") // (nothing printed)
}
