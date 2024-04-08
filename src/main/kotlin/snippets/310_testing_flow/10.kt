package f_310_testing_flow.s_10

private val infiniteFlow =
    flow<Nothing> {
        while (true) {
            delay(100)
        }
    }

class MessagesServiceTest {
    // ...
    
    @Test
    fun `should start at most one connection`() = runTest {
        // given
        var connectionsCounter = 0
        val source = infiniteFlow
            .onStart { connectionsCounter++ }
            .onCompletion { connectionsCounter-- }
        val service = MessagesService(
            messagesSource = source,
            scope = backgroundScope,
        )
        
        // when
        service.observeMessages("0")
            .launchIn(backgroundScope)
        service.observeMessages("1")
            .launchIn(backgroundScope)
        service.observeMessages("0")
            .launchIn(backgroundScope)
        service.observeMessages("2")
            .launchIn(backgroundScope)
        delay(1000)
        
        // then
        assertEquals(1, connectionsCounter)
    }
}
