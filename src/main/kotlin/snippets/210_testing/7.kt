package f_210_testing.s_7

@Test
fun `should map async and keep elements order`() = runTest {
    val transforms = listOf(
        suspend { delay(3000); "A" },
        suspend { delay(2000); "B" },
        suspend { delay(4000); "C" },
        suspend { delay(1000); "D" },
    )
    
    val res = transforms.mapAsync { it() }
    assertEquals(listOf("A", "B", "C", "D"), res)
    assertEquals(4000, currentTime)
}
