```
//1
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class FetchUserUseCase(
    private val repo: UserDataRepository,
) {

    suspend fun fetchUserData(): User = coroutineScope {
        val name = async { repo.getName() }
        val friends = async { repo.getFriends() }
        val profile = async { repo.getProfile() }
        User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await()
        )
    }
}

class FetchUserDataTest {

    @Test
    fun `should construct user`() = runBlocking {
        // given
        val repo = FakeUserDataRepository()
        val useCase = FetchUserUseCase(repo)

        // when
        val result = useCase.fetchUserData()

        // then
        val expectedUser = User(
            name = "Ben",
            friends = listOf(Friend("some-friend-id-1")),
            profile = Profile("Example description")
        )
        assertEquals(expectedUser, result)
    }

    class FakeUserDataRepository : UserDataRepository {
        override suspend fun getName(): String = "Ben"

        override suspend fun getFriends(): List<Friend> =
            listOf(Friend("some-friend-id-1"))

        override suspend fun getProfile(): Profile =
            Profile("Example description")
    }
}


interface UserDataRepository {
    suspend fun getName(): String
    suspend fun getFriends(): List<Friend>
    suspend fun getProfile(): Profile
}

data class User(
    val name: String,
    val friends: List<Friend>,
    val profile: Profile
)

data class Friend(val id: String)
data class Profile(val description: String)
```


```
class UserTests : KtAcademyFacadeTest() {

    @Test
    fun `should modify user details`() = runBlocking {
        // given
        thereIsUser(aUserToken, aUserId)

        // when
        facade.updateUserSelf(
            aUserToken,
            PatchUserSelfRequest(
                bio = aUserBio,
                bioPl = aUserBioPl,
                publicKey = aUserPublicKey,
                customImageUrl = aCustomImageUrl
            )
        )

        // then
        with(findUser(aUserId)) {
            assertEquals(aUserBio, bio)
            assertEquals(aUserBioPl, bioPl)
            assertEquals(aUserPublicKey, publicKey)
            assertEquals(aCustomImageUrl, customImageUrl)
        }
    }

    //...
}
```


```
suspend fun produceCurrentUserSync(): User {
    val profile = repo.getProfile()
    val friends = repo.getFriends()
    return User(profile, friends)
}

suspend fun produceCurrentUserAsync(): User = coroutineScope {
    val profile = async { repo.getProfile() }
    val friends = async { repo.getFriends() }
    User(profile.await(), friends.await())
}
```


```
class FakeDelayedUserDataRepository : UserDataRepository {

    override suspend fun getProfile(): Profile {
        delay(1000)
        return Profile("Example description")
    }

    override suspend fun getFriends(): List<Friend> {
        delay(1000)
        return listOf(Friend("some-friend-id-1"))
    }
}
```


```
fun main() {
    val scheduler = TestCoroutineScheduler()

    println(scheduler.currentTime) // 0
    scheduler.advanceTimeBy(1_000)
    println(scheduler.currentTime) // 1000
    scheduler.advanceTimeBy(1_000)
    println(scheduler.currentTime) // 2000
}
```


```
fun main() {
    val scheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(scheduler)

    CoroutineScope(testDispatcher).launch {
        println("Some work 1")
        delay(1000)
        println("Some work 2")
        delay(1000)
        println("Coroutine done")
    }

    CoroutineScope(testDispatcher).launch {
        delay(500)
        println("Different work")
    }

    println("[${scheduler.currentTime}] Before")
    scheduler.advanceUntilIdle()
    println("[${scheduler.currentTime}] After")
}
// [0] Before
// Some work 1
// Different work
// Some work 2
// Coroutine done
// [2000] After
```


```
fun main() {
    val dispatcher = StandardTestDispatcher()

    CoroutineScope(dispatcher).launch {
        println("Some work 1")
        delay(1000)
        println("Some work 2")
        delay(1000)
        println("Coroutine done")
    }

    println("[${dispatcher.scheduler.currentTime}] Before")
    dispatcher.scheduler.advanceUntilIdle()
    println("[${dispatcher.scheduler.currentTime}] After")
}
// [0] Before
// Some work 1
// Some work 2
// Coroutine done
// [2000] After
```


```
fun main() {
    val testDispatcher = StandardTestDispatcher()

    runBlocking(testDispatcher) {
        delay(1)
        println("Coroutine done")
    }
}
// (code runs forever)
```


```
fun main() {
    val testDispatcher = StandardTestDispatcher()

    CoroutineScope(testDispatcher).launch {
        delay(1)
        println("Done1")
    }
    CoroutineScope(testDispatcher).launch {
        delay(2)
        println("Done2")
    }
    testDispatcher.scheduler.advanceTimeBy(2) // Done
    testDispatcher.scheduler.runCurrent() // Done2
}
```


```
fun main() {
    val testDispatcher = StandardTestDispatcher()

    CoroutineScope(testDispatcher).launch {
        delay(2)
        print("Done")
    }

    CoroutineScope(testDispatcher).launch {
        delay(4)
        print("Done2")
    }

    CoroutineScope(testDispatcher).launch {
        delay(6)
        print("Done3")
    }

    for (i in 1..5) {
        print(".")
        testDispatcher.scheduler.advanceTimeBy(1)
        testDispatcher.scheduler.runCurrent()
    }
}
// ..Done..Done2.
```


```
fun main() {
    val dispatcher = StandardTestDispatcher()

    CoroutineScope(dispatcher).launch {
        delay(1000)
        println("Coroutine done")
    }

    Thread.sleep(Random.nextLong(2000)) // Does not matter
    // how much time we wait here, it will not influence
    // the result

    val time = measureTimeMillis {
       println("[${dispatcher.scheduler.currentTime}] Before")
       dispatcher.scheduler.advanceUntilIdle()
       println("[${dispatcher.scheduler.currentTime}] After")
    }
    println("Took $time ms")
}
// [0] Before
// Coroutine done
// [1000] After
// Took 15 ms (or other small number)
```


```
fun main() {
    val scope = TestScope()

    scope.launch {
        delay(1000)
        println("First done")
        delay(1000)
        println("Coroutine done")
    }

    println("[${scope.currentTime}] Before") // [0] Before
    scope.advanceTimeBy(1000)
    scope.runCurrent() // First done
    println("[${scope.currentTime}] Middle") // [1000] Middle
    scope.advanceUntilIdle() // Coroutine done
    println("[${scope.currentTime}] After") // [2000] After
}
```


```
//2
class TestTest {

    @Test
    fun test1() = runTest {
        assertEquals(0, currentTime)
        delay(1000)
        assertEquals(1000, currentTime)
    }

    @Test
    fun test2() = runTest {
        assertEquals(0, currentTime)
        coroutineScope {
            launch { delay(1000) }
            launch { delay(1500) }
            launch { delay(2000) }
        }
        assertEquals(2000, currentTime)
    }
}
```


```
//3
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
```


```
//4
class FetchUserUseCase(
    private val repo: UserDataRepository,
) {

    suspend fun fetchUserData(): User = coroutineScope {
        val name = async { repo.getName() }
        val friends = async { repo.getFriends() }
        val profile = async { repo.getProfile() }
        User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await()
        )
    }
}

class FetchUserDataTest {

    @Test
    fun `should load data concurrently`() = runTest {
        // given
        val userRepo = FakeUserDataRepository()
        val useCase = FetchUserUseCase(userRepo)

        // when
        useCase.fetchUserData()

        // then
        assertEquals(1000, currentTime)
    }

    @Test
    fun `should construct user`() = runTest {
        // given
        val userRepo = FakeUserDataRepository()
        val useCase = FetchUserUseCase(userRepo)

        // when
        val result = useCase.fetchUserData()

        // then
        val expectedUser = User(
            name = "Ben",
            friends = listOf(Friend("some-friend-id-1")),
            profile = Profile("Example description")
        )
        assertEquals(expectedUser, result)
    }

    class FakeUserDataRepository : UserDataRepository {
        override suspend fun getName(): String {
            delay(1000)
            return "Ben"
        }

        override suspend fun getFriends(): List<Friend> {
            delay(1000)
            return listOf(Friend("some-friend-id-1"))
        }

        override suspend fun getProfile(): Profile {
            delay(1000)
            return Profile("Example description")
        }
    }
}

interface UserDataRepository {
    suspend fun getName(): String
    suspend fun getFriends(): List<Friend>
    suspend fun getProfile(): Profile
}

data class User(
    val name: String,
    val friends: List<Friend>,
    val profile: Profile
)

data class Friend(val id: String)
data class Profile(val description: String)
```


```
//5
@Test
fun `should increment counter`() = runTest {
    var i = 0
    launch {
        while (true) {
            delay(1000)
            i++
        }
    }
    
    delay(1001)
    assertEquals(1, i)
    delay(1000)
    assertEquals(2, i)
    
    // Test would pass if we added
    // coroutineContext.job.cancelChildren()
}
```


```
//6
@Test
fun `should increment counter`() = runTest {
    var i = 0
    backgroundScope.launch {
        while (true) {
            delay(1000)
            i++
        }
    }
    
    
    delay(1001)
    assertEquals(1, i)
    delay(1000)
    assertEquals(2, i)
}
```


```
suspend fun <T, R> Iterable<T>.mapAsync(
    transformation: suspend (T) -> R
): List<R> = coroutineScope {
    this@mapAsync.map { async { transformation(it) } }
        .awaitAll()
}
```


```
//7
@Test
fun `should map async and keep elements order`() = runTest {
    val transforms = listOf(
        suspend { delay(3000); "A" },
        suspend { delay(2000); "B" },
        suspend { delay(4000); "C" },
        suspend { delay(1000); "D" },
    )
    
    val res = transforms.mapAsync { it() }
    assertEquals(listOf("A", "B", "C", "D"), res)
    assertEquals(4000, currentTime)
}
```


```
//8
@Test
fun `should support context propagation`() = runTest {
    var ctx: CoroutineContext? = null
    
    val name1 = CoroutineName("Name 1")
    withContext(name1) {
        listOf("A").mapAsync {
            ctx = currentCoroutineContext()
            it
        }
        assertEquals(name1, ctx?.get(CoroutineName))
    }
    
    val name2 = CoroutineName("Some name 2")
    withContext(name2) {
        listOf(1, 2, 3).mapAsync {
            ctx = currentCoroutineContext()
            it
        }
        assertEquals(name2, ctx?.get(CoroutineName))
    }
}
```


```
//9
@Test
fun `should support cancellation`() = runTest {
    var job: Job? = null
    
    val parentJob = launch {
        listOf("A").mapAsync {
            job = currentCoroutineContext().job
            delay(Long.MAX_VALUE)
        }
    }
    
    
    delay(1000)
    parentJob.cancel()
    assertEquals(true, job?.isCancelled)
}
```


```
// Incorrect implementation, that would make above tests fail
suspend fun <T, R> Iterable<T>.mapAsync(
    transformation: suspend (T) -> R
): List<R> =
    this@mapAsync
        .map { GlobalScope.async { transformation(it) } }
        .awaitAll()
```


```
fun main() {
    CoroutineScope(StandardTestDispatcher()).launch {
        print("A")
        delay(1)
        print("B")
    }
    CoroutineScope(UnconfinedTestDispatcher()).launch {
        print("C")
        delay(1)
        print("D")
    }
}
// C
```


```
//10
@Test
fun testName() = runTest(UnconfinedTestDispatcher()) {
    //...
}
```


```
//11
@Test
fun `should load data concurrently`() = runTest {
    // given
    val userRepo = mockk<UserDataRepository>()
    coEvery { userRepo.getName() } coAnswers {
        delay(600)
        aName
    }
    coEvery { userRepo.getFriends() } coAnswers {
        delay(700)
        someFriends
    }
    coEvery { userRepo.getProfile() } coAnswers {
        delay(800)
        aProfile
    }
    val useCase = FetchUserUseCase(userRepo)

    // when
    useCase.fetchUserData()

    // then
    assertEquals(800, currentTime)
}
```


```
suspend fun readSave(name: String): GameState =
    withContext(Dispatchers.IO) {
        reader.readCsvBlocking(name, GameState::class.java)
    }

suspend fun calculateModel() =
    withContext(Dispatchers.Default) {
        model.fit(
            dataset = newTrain,
            epochs = 10,
            batchSize = 100,
            verbose = false
        )
    }
```


```
suspend fun fetchUserData() = withContext(Dispatchers.IO) {
    val userId = readUserId() // blocking call
    val name = async { userRepo.getName(userId) }
    val friends = async { userRepo.getFriends(userId) }
    val profile = async { userRepo.getProfile(userId) }
    User(
        name = name.await(),
        friends = friends.await(),
        profile = profile.await()
    )
}
```


```
class FetchUserUseCase(
    private val userRepo: UserDataRepository,
    private val ioDispatcher: CoroutineDispatcher =
        Dispatchers.IO
) {

    suspend fun fetchUserData() = withContext(ioDispatcher) {
        val userId = readUserId() // blocking call
        val name = async { userRepo.getName(userId) }
        val friends = async { userRepo.getFriends(userId) }
        val profile = async { userRepo.getProfile(userId) }
        User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await()
        )
    }
}
```


```
//12
// inside runTest
val testDispatcher = this
    .coroutineContext[ContinuationInterceptor]
    as CoroutineDispatcher
// or
//val testDispatcher = this
//    .coroutineContext[CoroutineDispatcher]

val useCase = FetchUserUseCase(
    userRepo = userRepo,
    ioDispatcher = testDispatcher,
)
```


```
val useCase = FetchUserUseCase(
    userRepo = userRepo,
    ioDispatcher = EmptyCoroutineContext,
)
```


```
suspend fun sendUserData() {
    val userData = database.getUserData()
    progressBarVisible.value = true
    userRepository.sendUserData(userData)
    progressBarVisible.value = false
}
```


```
//13
@Test
fun `should show progress bar when sending data`() = runTest {
    // given
    val database = FakeDatabase()
    val vm = UserViewModel(database)

    // when
    launch {
        vm.sendUserData()
    }

    // then
    assertEquals(false, vm.progressBarVisible.value)

    // when
    advanceTimeBy(1000)

    // then
    assertEquals(false, vm.progressBarVisible.value)

    // when
    runCurrent()

    // then
    assertEquals(true, vm.progressBarVisible.value)

    // when
    advanceUntilIdle()

    // then
    assertEquals(false, vm.progressBarVisible.value)
}
```


```
//14
@Test
fun `should show progress bar when sending data`() = runTest {
    val database = FakeDatabase()
    val vm = UserViewModel(database)
    launch {
        vm.showUserData()
    }

    // then
    assertEquals(false, vm.progressBarVisible.value)
    delay(1000)
    assertEquals(true, vm.progressBarVisible.value)
    delay(1000)
    assertEquals(false, vm.progressBarVisible.value)
}
```


```
@Scheduled(fixedRate = 5000)
fun sendNotifications() {
    notificationsScope.launch {
        val notifications = notificationsRepository
            .notificationsToSend()
        for (notification in notifications) {
            launch {
                notificationsService.send(notification)
                notificationsRepository
                    .markAsSent(notification.id)
            }
        }
    }
}
```


```
@Test
fun testSendNotifications() {
    // given
    val notifications = List(100) { Notification(it) }
    val repo = FakeNotificationsRepository(
        delayMillis = 200,
        notifications = notifications,
    )
    val service = FakeNotificationsService(
        delayMillis = 300,
    )
    val testScope = TestScope()
    val sender = NotificationsSender(
        notificationsRepository = repo,
        notificationsService = service,
        notificationsScope = testScope
    )

    // when
    sender.sendNotifications()
    testScope.advanceUntilIdle()

    // then all notifications are sent and marked
    assertEquals(
        notifications.toSet(),
        service.notificationsSent.toSet()
    )
    assertEquals(
        notifications.map { it.id }.toSet(),
        repo.notificationsMarkedAsSent.toSet()
    )

    // and notifications are sent concurrently
    assertEquals(700, testScope.currentTime)
}
```


```
class BaseUnitTest {
    lateinit var scheduler: TestCoroutineScheduler
    lateinit var dispatcher: TestDispatcher

    @Before
    fun setUp() {
        scheduler = TestCoroutineScheduler()
        dispatcher = StandardTestDispatcher(scheduler)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ...
}
```


```
class MainViewModel(
    private val userRepo: UserRepository,
    private val newsRepo: NewsRepository,
) : BaseViewModel() {
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>> = _news

    private val _progressVisible = MutableLiveData<Boolean>()
    val progressVisible: LiveData<Boolean> = _progressVisible

    fun onCreate() {
        viewModelScope.launch {
            val user = userRepo.getUser()
            _userName.value = user.name
        }
        viewModelScope.launch {
            _progressVisible.value = true
            val news = newsRepo.getNews()
                .sortedByDescending { it.date }
            _news.value = news
            _progressVisible.value = false
        }
    }
}
```


```
class MainViewModelTests {
    private lateinit var scheduler: TestCoroutineScheduler
    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        scheduler = TestCoroutineScheduler()
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        viewModel = MainViewModel(
            userRepo = FakeUserRepository(aName),
            newsRepo = FakeNewsRepository(someNews)
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        viewModel.onCleared()
    }

    @Test
    fun `should show user name and sorted news`() {
        // when
        viewModel.onCreate()
        scheduler.advanceUntilIdle()

        // then
        assertEquals(aName, viewModel.userName.value)
        val someNewsSorted =
            listOf(News(date1), News(date2), News(date3))
        assertEquals(someNewsSorted, viewModel.news.value)
    }

    @Test
    fun `should show progress bar when loading news`() {
        // given
        assertEquals(null, viewModel.progressVisible.value)

        // when
        viewModel.onCreate()

        // then
        assertEquals(false, viewModel.progressVisible.value)

        // when
        scheduler.runCurrent()

        // then
        assertEquals(true, viewModel.progressVisible.value)

        // when
        scheduler.advanceTimeBy(200)

        // then
        assertEquals(true, viewModel.progressVisible.value)

        // when
        scheduler.runCurrent()

        // then
        assertEquals(false, viewModel.progressVisible.value)
    }

    @Test
    fun `user and news are called concurrently`() {
        // when
        viewModel.onCreate()
        scheduler.advanceUntilIdle()

        // then
        assertEquals(300, testDispatcher.currentTime)
    }

    class FakeUserRepository(
        private val name: String
    ) : UserRepository {
        override suspend fun getUser(): UserData {
            delay(300)
            return UserData(name)
        }
    }

    class FakeNewsRepository(
        private val news: List<News>
    ) : NewsRepository {
        override suspend fun getNews(): List<News> {
            delay(200)
            return news
        }
    }
}
```


```
class MainCoroutineRule : TestWatcher() {
    lateinit var scheduler: TestCoroutineScheduler
        private set
    lateinit var dispatcher: TestDispatcher
        private set

    override fun starting(description: Description) {
        scheduler = TestCoroutineScheduler()
        dispatcher = StandardTestDispatcher(scheduler)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```


```
class MainViewModelTests {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // ...

    @Test
    fun `should show user name and sorted news`() {
        // when
        viewModel.onCreate()
        mainCoroutineRule.scheduler.advanceUntilIdle()

        // then
        assertEquals(aName, viewModel.userName.value)
        val someNewsSorted =
            listOf(News(date1), News(date2), News(date3))
        assertEquals(someNewsSorted, viewModel.news.value)
    }

    @Test
    fun `should show progress bar when loading news`() {
        // given
        assertEquals(null, viewModel.progressVisible.value)

        // when
        viewModel.onCreate()

        // then
        assertEquals(true, viewModel.progressVisible.value)

        // when
        mainCoroutineRule.scheduler.advanceTimeBy(200)

        // then
        assertEquals(false, viewModel.progressVisible.value)
    }

    @Test
    fun `user and news are called concurrently`() {
        // when
        viewModel.onCreate()

        mainCoroutineRule.scheduler.advanceUntilIdle()

        // then
        assertEquals(300, mainCoroutineRule.currentTime)
    }
}
```


```
@ExperimentalCoroutinesApi
class MainCoroutineExtension :
    BeforeEachCallback, AfterEachCallback {

    lateinit var scheduler: TestCoroutineScheduler
        private set
    lateinit var dispatcher: TestDispatcher
        private set

    override fun beforeEach(context: ExtensionContext?) {
        scheduler = TestCoroutineScheduler()
        dispatcher = StandardTestDispatcher(scheduler)
        Dispatchers.setMain(dispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}
```


```
@JvmField
@RegisterExtension
var mainCoroutineExtension = MainCoroutineExtension()
```