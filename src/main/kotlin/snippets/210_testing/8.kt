package f_210_testing.s_8

@Test
fun `should support context propagation`() = runTest {
    var ctx: CoroutineContext? = null
    
    val name1 = CoroutineName("Name 1")
    withContext(name1) {
        listOf("A").mapAsync {
            ctx = currentCoroutineContext()
            it
        }
        assertEquals(name1, ctx?.get(CoroutineName))
    }
    
    val name2 = CoroutineName("Some name 2")
    withContext(name2) {
        listOf(1, 2, 3).mapAsync {
            ctx = currentCoroutineContext()
            it
        }
        assertEquals(name2, ctx?.get(CoroutineName))
    }
}
