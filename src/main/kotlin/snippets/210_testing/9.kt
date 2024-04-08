package f_210_testing.s_9

@Test
fun `should support cancellation`() = runTest {
    var job: Job? = null
    
    val parentJob = launch {
        listOf("A").mapAsync {
            job = currentCoroutineContext().job
            delay(Long.MAX_VALUE)
        }
    }
    
    
    delay(1000)
    parentJob.cancel()
    assertEquals(true, job?.isCancelled)
}
