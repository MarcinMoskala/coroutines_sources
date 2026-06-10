package f_209_state.s_7

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Volatile
var num = 0

suspend fun main() {
    coroutineScope {
        repeat(10_000) {
            launch {
                delay(10)
                num++
            }
        }
    }
    print(num) // around 9800, not 10000
}
