package f_209_state.s_2

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class User(val name: String)

suspend fun main() {
    var users = listOf<User>()
    coroutineScope {
        repeat(10_000) { i ->
            launch {
                delay(10)
                users += User("User$i")
            }
        }
    }
    print(users.size)
}
