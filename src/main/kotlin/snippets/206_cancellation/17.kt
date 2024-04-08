package f_206_cancellation.s_17

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

class Test {
    @Test
    fun testTime2() = runTest {
        withTimeout(1000) {
            // something that should take less than 1000
            delay(900) // virtual time
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun testTime1() = runTest {
        withTimeout(1000) {
            // something that should take more than 1000
            delay(1100) // virtual time
        }
    }

    @Test
    fun testTime3() = runBlocking {
        withTimeout(1000) {
            // normal test, that should not take too long
            delay(900) // really waiting 900 ms
        }
    }
}
