package f_206_cancellation.s_14

import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

// Poor practice, do not do this
class UserNotFoundException : CancellationException()

suspend fun main() {
    try {
        updateUserData()
    } catch (e: UserNotFoundException) {
        println("User not found")
    }
}

suspend fun updateUserData() = coroutineScope {
    launch { updateUser() }
    launch { updateTweets() }
}
suspend fun updateTweets() { 
    delay(1000)
    println("Updating...") 
}
suspend fun updateUser() { throw UserNotFoundException() }
// (1 sec)
// Updating...
