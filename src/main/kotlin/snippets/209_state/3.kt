package f_209_state.s_3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

//sampleStart
var counter = 0

fun main() = runBlocking {
    val lock = Any()
    massiveRun {
        synchronized(lock) { // We are blocking threads!
            counter++
        }
    }
    println("Counter = $counter") // 1000000
}
//sampleEnd

suspend fun massiveRun(action: suspend () -> Unit) =
    withContext(Dispatchers.Default) {
        repeat(1000) {
            launch {
                repeat(1000) { action() }
            }
        }
    }
