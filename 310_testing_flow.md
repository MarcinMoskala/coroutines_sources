```
class ObserveAppointmentsService(
    private val appointmentRepository: AppointmentRepository
) {
    fun observeAppointments(): Flow<List<Appointment>> =
        appointmentRepository
            .observeAppointments()
            .filterIsInstance<AppointmentsUpdate>()
            .map { it.appointments }
            .distinctUntilChanged()
            .retry {
                it is ApiException && it.code in 500..599
            }
}
```


```
class FakeAppointmentRepository(
    private val flow: Flow<AppointmentsEvent>
) : AppointmentRepository {
    override fun observeAppointments() = flow
}

class ObserveAppointmentsServiceTest {
    val aDate1 = Instant.parse("2020-08-30T18:43:00Z")
    val anAppointment1 = Appointment("APP1", aDate1)
    val aDate2 = Instant.parse("2020-08-31T18:43:00Z")
    val anAppointment2 = Appointment("APP2", aDate2)
    
    @Test
    fun `should keep only appointments from...`() = runTest {
        // given
        val repo = FakeAppointmentRepository(
            flowOf(
                AppointmentsConfirmed,
                AppointmentsUpdate(listOf(anAppointment1)),
                AppointmentsUpdate(listOf(anAppointment2)),
                AppointmentsConfirmed,
            )
        )
        val service = ObserveAppointmentsService(repo)
        
        // when
        val result = service.observeAppointments().toList()
        
        // then
        assertEquals(
            listOf(
                listOf(anAppointment1),
                listOf(anAppointment2),
            ),
            result
        )
    }
    
    // ...
}
```


```
class ObserveAppointmentsService(
    private val appointmentRepository: AppointmentRepository
) {
    fun observeAppointments(): Flow<List<Appointment>> =
        appointmentRepository
            .observeAppointments()
            .onEach { delay(1000) } // Will not influence
            // the above test
            .filterIsInstance<AppointmentsUpdate>()
            .map { it.appointments }
            .distinctUntilChanged()
            .retry {
                it is ApiException && it.code in 500..599
            }
}
```


```
class ObserveAppointmentsService(
    private val appointmentRepository: AppointmentRepository,
) {
    // Don't do that!
    fun observeAppointments(): Flow<List<Appointment>> =
        flow {
            val list = appointmentRepository
                .observeAppointments()
                .filterIsInstance<AppointmentsUpdate>()
                .map { it.appointments }
                .distinctUntilChanged()
                .retry {
                    it is ApiException && it.code in 500..599
                }
                .toList()
            emitAll(list)
        }
}
```


```
class ObserveAppointmentsServiceTest {
    // ...
    
    @Test
    fun `should eliminate elements that are...`() = runTest {
        // given
        val repo = FakeAppointmentRepository(flow {
            delay(1000)
            emit(AppointmentsUpdate(listOf(anAppointment1)))
            emit(AppointmentsUpdate(listOf(anAppointment1)))
            delay(1000)
            emit(AppointmentsUpdate(listOf(anAppointment2)))
            delay(1000)
            emit(AppointmentsUpdate(listOf(anAppointment2)))
            emit(AppointmentsUpdate(listOf(anAppointment1)))
        })
        val service = ObserveAppointmentsService(repo)
        
        // when
        val result = service.observeAppointments()
            .map { currentTime to it }
            .toList()
        
        // then
        assertEquals(
            listOf(
                1000L to listOf(anAppointment1),
                2000L to listOf(anAppointment2),
                3000L to listOf(anAppointment1),
            ), result
        )
    }
    
    // ...
}
```


```
class ObserveAppointmentsServiceTest {
    // ...
    
    @Test
    fun `should retry when API exception...`() = runTest {
        // given
        val repo = FakeAppointmentRepository(flow {
            emit(AppointmentsUpdate(listOf(anAppointment1)))
            throw ApiException(502, "Some message")
        })
        val service = ObserveAppointmentsService(repo)
        
        // when
        val result = service.observeAppointments()
            .take(3)
            .toList()
        
        // then
        assertEquals(
            listOf(
                listOf(anAppointment1),
                listOf(anAppointment1),
                listOf(anAppointment1),
            ), result
        )
    }
}
```


```
class ObserveAppointmentsServiceTest {
    // ...
    
    @Test
    fun `should retry when API exception with the code 5XX`() = runTest {
        // given
        var retried = false
        val someException = object : Exception() {}
        val repo = FakeAppointmentRepository(flow {
            emit(AppointmentsUpdate(listOf(anAppointment1)))
            if (!retried) {
                retried = true
                throw ApiException(502, "Some message")
            } else {
                throw someException
            }
        })
        val service = ObserveAppointmentsService(repo)
        
        // when
        val result = service.observeAppointments()
            .catch<Any> { emit(it) }
            .toList()
        
        // then
        assertTrue(retried)
        assertEquals(
            listOf(
                listOf(anAppointment1),
                listOf(anAppointment1),
                someException,
            ), result
        )
    }
}
```


```
class MessagesService(
    messagesSource: Flow<Message>,
    scope: CoroutineScope
) {
    private val source = messagesSource
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed()
        )
    
    fun observeMessages(fromUserId: String) = source
        .filter { it.fromUserId == fromUserId }
}
```


```
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
```


```
class MessagesServiceTest {
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
            .take(2)
            .toList()
        
        // then
        assertEquals(
            listOf(
                Message(fromUserId = "0", text = "A"),
                Message(fromUserId = "0", text = "C"),
            ), result
        )
    }
}
```


```
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
```


```
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
```


```
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
        val messagesTurbine = service.observeMessages("0")
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
```


```
// Starts at most one connection to the source
class MessagesService(
    messagesSource: Flow<Message>,
    scope: CoroutineScope
) {
    private val source = messagesSource
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed()
        )
    
    fun observeMessages(fromUserId: String) = source
        .filter { it.fromUserId == fromUserId }
}

// Can start multiple connections to the source
class MessagesService(
    messagesSource: Flow<Message>,
) {
    fun observeMessages(fromUserId: String) = messagesSource
        .filter { it.fromUserId == fromUserId }
}
```


```
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
```


```
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
```