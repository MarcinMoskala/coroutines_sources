package f_209_state.s_1

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
    var num = 0
    coroutineScope {
        repeat(10_000) {
            launch { // uses Dispatchers.Default
                delay(10)
                num++
            }
        }
    }
    print(num)
}
