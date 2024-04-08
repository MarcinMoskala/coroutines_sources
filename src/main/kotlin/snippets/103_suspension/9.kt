package f_103_suspension.s_9

import kotlin.concurrent.thread
import kotlin.coroutines.*

data class User(val name: String)

fun requestUser(callback: (User) -> Unit) {
    thread {
        Thread.sleep(1000)
        callback.invoke(User("Test"))
    }
}

suspend fun main() {
    println("Before")
    val user = suspendCancellableCoroutine<User> { cont ->
        requestUser { user ->
            cont.resume(user)
        }
    }
    println(user)
    println("After")
}
