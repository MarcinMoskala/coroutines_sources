package f_310_testing_flow.s_7

class MessagesServiceTest {
    @Test
    fun `should emit messages from user`() = runTest {
        // given
        val source = flow {
            emit(Message(fromUserId = "0", text = "A"))
            delay(1000)
            emit(Message(fromUserId = "1", text = "B"))
            emit(Message(fromUserId = "0", text = "C"))
        }
        val service = MessagesService(
            messagesSource = source,
            scope = backgroundScope,
        )
        
        // when
        val emittedMessages = mutableListOf<Message>()
        service.observeMessages("0")
            .onEach { emittedMessages.add(it) }
            .launchIn(backgroundScope)
        delay(1)
        
        // then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
            ), emittedMessages
        )
        
        // when
        delay(1000)
        
        // then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
                Message(fromUserId = "0", text = "C"),
            ), emittedMessages
        )
    }
}
