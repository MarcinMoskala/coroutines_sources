package f_307_flow_lifecycle.s_12

import kotlinx.coroutines.flow.*

class MyError : Throwable("My error")

val flow = flow {
    emit("Message1")
    emit("Message2")
}

suspend fun main(): Unit {
    flow.onStart { println("Before") }
        .onEach { throw MyError() }
        .catch { println("Caught $it") }
        .collect()
}
// Before
// Caught MyError: My error
