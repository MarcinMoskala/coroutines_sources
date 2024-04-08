package f_210_testing.s_12

val testDispatcher = this
    .coroutineContext[ContinuationInterceptor]
    as CoroutineDispatcher

val useCase = FetchUserUseCase(
    userRepo = userRepo,
    ioDispatcher = testDispatcher,
)
