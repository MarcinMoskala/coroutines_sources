package f_206_cancellation.s_13

import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

class UserNotFoundException : CancellationException()

suspend fun main() {
    try {
        updateUserData()
    } catch (e: UserNotFoundException) {
        println("User not found")
    }
}

suspend fun updateUserData() {
    updateUser()
    updateTweets()
}
suspend fun updateTweets() { 
    delay(1000)
    println("Updating...") 
}
suspend fun updateUser() { throw UserNotFoundException() }
