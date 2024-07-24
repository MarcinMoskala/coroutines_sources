package f_209_state.s_16

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

suspend fun main() {
    val mutex = Mutex()
    var num = 0
    coroutineScope {
        repeat(10_000) {
            launch {
                mutex.withLock {
                    num++
                }
            }
        }
    }
    print(num) // 10000
}
