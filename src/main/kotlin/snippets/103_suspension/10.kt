package f_103_suspension.s_10

import kotlin.concurrent.thread
import kotlin.coroutines.*

data class User(val name: String)

fun requestUser(callback: (User) -> Unit) {
    thread {
        Thread.sleep(1000)
        callback.invoke(User("Test"))
    }
}

//sampleStart
suspend fun main() {
    println("Before")
    val user = suspendCoroutine<User> { cont ->
        requestUser { user ->
            cont.resume(user)
        }
    }
    println(user)
    println("After")
}
// Before
// (1 second delay)
// User(name=Test)
// After
//sampleEnd
