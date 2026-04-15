package f_309_state_flow.s_4

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun main(): Unit = coroutineScope {
    val flow = flowOf("A", "B", "C")
        .onEach { delay(1000) }

    val sharedFlow: SharedFlow<String> = flow.shareIn(
        scope = this,
        started = SharingStarted.Eagerly,
        // replay = 0 (default)
    )

    delay(500)

    launch {
        sharedFlow.collect { println("#1 $it") }
    }

    delay(1000)

    launch {
        sharedFlow.collect { println("#2 $it") }
    }

    delay(1000)

    launch {
        sharedFlow.collect { println("#3 $it") }
    }
}
