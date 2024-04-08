package f_401_3_use_cases.s_2

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
annotation class Test

suspend fun main() = coroutineScope {
    // ...
}

class SomeTests {
    @Test
    fun someTest() = runTest {
        // ...
    }
}
