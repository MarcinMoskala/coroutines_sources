package f_209_state.s_14

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MessagesRepository {
    private val messages = mutableListOf<String>()
    private val dispatcher = Dispatchers.IO
        .limitedParallelism(1)

    suspend fun add(message: String) =
        withContext(dispatcher) {
            delay(1000) // we simulate network call
            messages.add(message)
        }
}

suspend fun main() {
    val repo = MessagesRepository()

    val timeMillis = measureTimeMillis {
        coroutineScope {
            repeat(5) {
                launch {
                    repo.add("Message$it")
                }
            }
        }
    }
    println(timeMillis) // 1058
}
