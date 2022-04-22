package f_209_state.s_2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

//sampleStart
var counter = 0

fun main() = runBlocking {
    massiveRun {
        counter++
    }
    println(counter) // ~567231
}

suspend fun massiveRun(action: suspend () -> Unit) =
    withContext(Dispatchers.Default) {
        repeat(1000) {
            launch {
                repeat(1000) { action() }
            }
        }
    }
//sampleEnd
