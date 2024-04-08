package f_310_testing_flow.s_9

class MessagesServiceTest {
    @Test
    fun `should emit messages from user`() = runTest {
        turbineScope {
            // given
            val source = flow {
                emit(Message(fromUserId = "0", text = "A"))
                emit(Message(fromUserId = "1", text = "B"))
                emit(Message(fromUserId = "0", text = "C"))
            }
            val service = MessagesService(
                messagesSource = source,
                scope = backgroundScope,
            )

            // when
            val messagesTurbine = service
                .observeMessages("0")
                .testIn(backgroundScope)

            // then
            assertEquals(
                Message(fromUserId = "0", text = "A"),
                messagesTurbine.awaitItem()
            )
            assertEquals(
                Message(fromUserId = "0", text = "C"),
                messagesTurbine.awaitItem()
            )
            messagesTurbine.expectNoEvents()
        }
    }
}
