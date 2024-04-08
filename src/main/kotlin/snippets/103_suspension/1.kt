package f_103_suspension.s_1

import kotlinx.coroutines.*

// Suspending function can suspend a coroutine
suspend fun a() {
    // Suspends the coroutine for 1 second
    delay(1000)
    println("A")
}

// Suspending main is started by Kotlin in a coroutine
suspend fun main() {
    println("Before")
    a()
    println("After")
}
// Before
// (1 second delay)
// A
// After
