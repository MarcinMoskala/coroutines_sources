package f_307_flow_lifecycle.s_16

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*

suspend fun main(): Unit = coroutineScope {
    flowOf("User1", "User2")
        .onStart { println("Users:") }
        .onEach { println(it) }
        .launchIn(this)
}
// Users:
// User1
// User2
