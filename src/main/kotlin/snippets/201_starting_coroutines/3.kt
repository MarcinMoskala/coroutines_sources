package f_201_starting_coroutines.s_3

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

suspend fun main() {
    val value1 = GlobalScope.async {
        delay(2000L)
        1
    }
    val value2 = GlobalScope.async {
        delay(2000L)
        2
    }
    val value3 = GlobalScope.async {
        delay(2000L)
        3
    }
    println("Calculating")
    print(value1.await())
    print(value2.await())
    print(value3.await())
}
