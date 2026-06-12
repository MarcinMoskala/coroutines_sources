package f_209_state.s_13

import kotlinx.coroutines.*

suspend fun main() {
    var num = 0
    val dispatcher = Dispatchers.IO.limitedParallelism(1)
    coroutineScope {
        repeat(10_000) {
            launch(dispatcher) {
                delay(10)
                num++
            }
        }
    }
    print(num) // 10000
}
