package f_210_testing.s_6

@Test
fun `should increment counter`() = runTest {
    var i = 0
    backgroundScope.launch {
        while (true) {
            delay(1000)
            i++
        }
    }
    
    
    delay(1001)
    assertEquals(1, i)
    delay(1000)
    assertEquals(2, i)
}
