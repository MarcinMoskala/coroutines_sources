package f_209_state.s_9

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

var counter = ConcurrentHashMap<String, Int>()

fun increment(key: String) {
    val value = counter[key] ?: 0
    counter[key] = value + 1
}

suspend fun main() {
    coroutineScope {
        repeat(10_000) {
            launch { // uses Dispatchers.Default
                increment("A")
            }
        }
    }
    print(counter) // {A=7162}
}
