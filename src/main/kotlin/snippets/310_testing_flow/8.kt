package f_310_testing_flow.s_8

suspend fun <T> Flow<T>.toListDuring(
    duration: Duration
): List<T> = coroutineScope {
    val result = mutableListOf<T>()
    val job = launch {
        this@toListDuring.collect(result::add)
    }
    delay(duration)
    job.cancel()
    return@coroutineScope result
}

class MessagesServiceTest {
    @Test
    fun `should emit messages from user`() = runTest {
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
        val emittedMessages = service.observeMessages("0")
            .toListDuring(1.milliseconds)
        
        // then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
                Message(fromUserId = "0", text = "C"),
            ), emittedMessages
        )
    }
}
