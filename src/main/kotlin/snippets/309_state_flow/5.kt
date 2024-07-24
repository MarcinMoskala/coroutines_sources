package f_309_state_flow.s_5

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun main(): Unit = coroutineScope {
    val flow = flowOf("A", "B", "C")

    val sharedFlow: SharedFlow<String> = flow.shareIn(
        scope = this,
        started = SharingStarted.Eagerly,
    )

    delay(100)
    launch {
        sharedFlow.collect { println("#1 $it") }
    }
    print("Done")
}
