package f_204_dispatchers.s_10

class DiscUserRepository(
    private val discReader: DiscReader,
    private val dispatcher: CoroutineContext = Dispatchers.IO,
) : UserRepository {
    override suspend fun getUser(): UserData =
        withContext(dispatcher) {
            UserData(discReader.read("userName"))
        }
}

class UserReaderTests {

    @Test
    fun `some test`() = runTest {
        // given
        val discReader = FakeDiscReader()
        val repo = DiscUserRepository(
            discReader,
            // one of coroutines testing practices
            this.coroutineContext[ContinuationInterceptor]!!
        )
        //...
    }
}
