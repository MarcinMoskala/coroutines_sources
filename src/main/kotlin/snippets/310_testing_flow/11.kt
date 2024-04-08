package f_310_testing_flow.s_11

class ChatViewModel(
    private val messagesService: MessagesService,
) : ViewModel() {
    private val _lastMessage =
        MutableStateFlow<String?>(null)
    val lastMessage: StateFlow<String?> = _lastMessage
    
    private val _messages =
        MutableStateFlow(emptyList<String>())
    val messages: StateFlow<List<String>> = _messages
    
    fun start(fromUserId: String) {
        messagesService.observeMessages(fromUserId)
            .onEach {
                val text = it.text
                _lastMessage.value = text
                _messages.value = _messages.value + text
            }
            .launchIn(viewModelScope)
    }
}

class ChatViewModelTest {
    @Test
    fun `should expose messages from user`() = runTest {
        // given
        val source = MutableSharedFlow<Message>()
        
        // when
        val viewModel = ChatViewModel(
            messagesService = FakeMessagesService(source)
        )
        viewModel.start("0")
        
        // then
        assertEquals(null, viewModel.lastMessage.value)
        assertEquals(emptyList(), viewModel.messages.value)
        
        // when
        source.emit(Message(fromUserId = "0", text = "ABC"))
        
        // then
        assertEquals("ABC", viewModel.lastMessage.value)
        assertEquals(listOf("ABC"), viewModel.messages.value)
        
        // when
        source.emit(Message(fromUserId = "0", text = "DEF"))
        source.emit(Message(fromUserId = "1", text = "GHI"))
        
        // then
        assertEquals("DEF", viewModel.lastMessage.value)
        assertEquals(
            listOf("ABC", "DEF"),
            viewModel.messages.value
        )
    }
}
