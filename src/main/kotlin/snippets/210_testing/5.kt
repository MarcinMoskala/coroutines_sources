package f_210_testing.s_5

@Test
fun `should increment counter`() = runTest {
    var i = 0
    launch {
        while (true) {
            delay(1000)
            i++
        }
    }
    
    delay(1001)
    assertEquals(1, i)
    delay(1000)
    assertEquals(2, i)
    
    // Test would pass if we added
    // coroutineContext.job.cancelChildren()
}
