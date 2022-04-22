package f_209_state.s_7

import kotlinx.coroutines.*
import java.util.concurrent.Executors

//sampleStart
val dispatcher = Dispatchers.IO
    .limitedParallelism(1)

var counter = 0

fun main() = runBlocking {
    massiveRun {
        withContext(dispatcher) {
            counter++
        }
    }
    println(counter) // 1000000
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
