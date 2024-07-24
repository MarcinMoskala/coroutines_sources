package f_209_state.s_11

import kotlinx.coroutines.*

suspend fun main() {
    var num = 0
    val lock = Any()
    coroutineScope {
        repeat(10_000) {
            launch {
                delay(10)
                synchronized(lock) {
                    num++
                }
            }
        }
    }
    print(num) // 10000
}
