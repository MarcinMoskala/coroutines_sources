package f_206_cancellation.s_15

import kotlinx.coroutines.*

class UserNotFoundException : RuntimeException()

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
