package f_209_state.s_10

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

var counter = ConcurrentHashMap<String, Int>()

fun increment(key: String) {
    counter.compute(key) { _, v -> (v ?: 0) + 1 }
}

suspend fun main() {
    coroutineScope {
        repeat(10_000) {
            launch { // uses Dispatchers.Default
                increment("A")
            }
        }
    }
    print(counter) // {A=10000}
}
