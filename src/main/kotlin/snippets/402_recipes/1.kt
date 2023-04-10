package f_402_recipes.s_1

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

fun <T> suspendLazy(
    initializer: suspend () -> T
): suspend () -> T {
    var initializer: (suspend () -> T)? = initializer
    val mutex = Mutex()
    var holder: Any? = Any()
    
    return {
        if (initializer == null) holder as T
        else mutex.withLock {
            initializer?.let {
                holder = it()
                initializer = null
            }
            holder as T
        }
    }
}

// Example use
suspend fun makeConnection(): String {
    println("Creating connection")
    delay(1000)
    return "Connection"
}

val getConnection = suspendLazy { makeConnection() }

suspend fun main() {
    println(getConnection())
    println(getConnection())
    println(getConnection())
}
// Creating connection
// (1 sec)
// Connection
// Connection
// Connection
