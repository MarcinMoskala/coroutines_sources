package f_103_suspension.s_1

import kotlinx.coroutines.*

suspend fun a() {
    // Suspends the coroutine for 1 second
    delay(1000)
    println("A")
}

suspend fun main() {
    println("Before")
    a()
    println("After")
}
