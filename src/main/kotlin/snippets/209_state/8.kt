package f_209_state.s_8

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

data class User(val name: String)

suspend fun main() {
    val users = ConcurrentHashMap.newKeySet<User>()
    coroutineScope {
        for (i in 1..10000) {
            launch {
                delay(10)
                users += User("User$i")
            }
        }
    }
    println(users.size) // 10000
}
