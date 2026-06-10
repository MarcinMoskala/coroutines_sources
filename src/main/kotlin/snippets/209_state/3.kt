package f_209_state.s_3

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class User(val name: String)

suspend fun main() {
    val users = mutableListOf<User>()
    coroutineScope {
        for (i in 1..10000) {
            launch {
                delay(10)
                users += User("User$i")
            }
        }
    }
    println(users.size)
}
