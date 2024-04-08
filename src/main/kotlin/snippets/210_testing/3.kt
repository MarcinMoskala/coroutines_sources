package f_210_testing.s_3

private val userDataRepository = FakeDelayedUserDataRepository()
private val useCase = ProduceUserUseCase(userDataRepository)

@Test
fun `Should produce user synchronously`() = runTest {
    // when
    useCase.produceCurrentUserSync()

    // then
    assertEquals(2000, currentTime)
}

@Test
fun `Should produce user asynchronous`() = runTest {
    // when
    useCase.produceCurrentUserAsync()

    // then
    assertEquals(1000, currentTime)
}
