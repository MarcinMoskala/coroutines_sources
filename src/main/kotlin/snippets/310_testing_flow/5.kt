package f_310_testing_flow.s_5

class MessagesServiceTest {
    // Failing test!
    @Test
    fun `should emit messages from user`() = runTest {
        // given
        val source = flowOf(
            Message(fromUserId = "0", text = "A"),
            Message(fromUserId = "1", text = "B"),
            Message(fromUserId = "0", text = "C"),
        )
        val service = MessagesService(
            messagesSource = source,
            scope = backgroundScope,
        )
        
        // when
        val result = service.observeMessages("0")
            .toList() // Here we'll wait forever!
        
        // then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
                Message(fromUserId = "0", text = "C"),
            ), result
        )
    }
}
