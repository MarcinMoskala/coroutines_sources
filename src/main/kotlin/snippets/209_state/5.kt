package f_209_state.s_5

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

suspend fun main() {
    var str = AtomicReference("")
    coroutineScope {
        repeat(10_000) {
            launch {
                delay(10)
                str.updateAndGet { it + "A" }
            }
        }
    }
    print(str.get().length) // 10000
}
