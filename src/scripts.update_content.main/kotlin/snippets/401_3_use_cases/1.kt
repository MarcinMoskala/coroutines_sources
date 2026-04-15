package f_401_3_use_cases.s_1

import kotlinx.coroutines.runBlocking
annotation class Test

fun main() = runBlocking {
    // ...
}

class SomeTests {
    @Test
    fun someTest() = runBlocking {
        // ...
    }
}
