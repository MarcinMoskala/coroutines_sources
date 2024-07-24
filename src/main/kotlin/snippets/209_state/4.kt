package f_209_state.s_4

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

suspend fun main() {
    var num = AtomicInteger(0)
    coroutineScope {
        repeat(10_000) {
            launch {
                delay(10)
                num.incrementAndGet()
            }
        }
    }
    print(num) // 10000
}
