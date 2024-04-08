package f_209_state.s_15

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

suspend fun main() = coroutineScope {
    val semaphore = Semaphore(2)

    repeat(5) {
        launch {
            semaphore.withPermit {
                delay(1000)
                print(it)
            }
        }
    }
}
