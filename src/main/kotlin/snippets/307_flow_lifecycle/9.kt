package f_307_flow_lifecycle.s_9

import kotlinx.coroutines.flow.*

class MyError : Throwable("My error")

val flow = flow {
    emit("Message1")
    throw MyError()
}

suspend fun main(): Unit {
    flow.catch { emit("Error") }
        .collect { println("Collected $it") }
}
// Collected Message1
// Collected Error
