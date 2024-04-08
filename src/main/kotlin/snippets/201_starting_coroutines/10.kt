package f_201_starting_coroutines.s_10

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

suspend fun main() = coroutineScope {
    println("A")
    val a: Deferred<Int> = async {
        delay(1000L)
        10
    }
    println("B")
    val b: Deferred<Int> = async {
        delay(1000L)
        20
    }
    println("C")
    println(a.await() + b.await())
}
