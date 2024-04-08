package f_201_starting_coroutines.s_6

import kotlinx.coroutines.runBlocking
annotation class Test

fun main() = runBlocking {
    // ...
}

class MyTests {

    @Test
    fun `a test`() = runBlocking {

    }
}
