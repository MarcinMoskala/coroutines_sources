package f_307_flow_lifecycle.s_10

import kotlinx.coroutines.flow.*

class MyError : Throwable("My error")

val flow = flow {
    emit("Message1")
    throw MyError()
}

suspend fun main(): Unit {
    try {
        flow.collect { println("Collected $it") }
    } catch (e: MyError) {
        println("Caught")
    }
}
// Collected Message1
// Caught
