```
@Controller
class UserController(
    private val tokenService: TokenService,
    private val userService: UserService,
) {
    @GetMapping("/me")
    suspend fun findUser(
        @PathVariable userId: String,
        @RequestHeader("Authorization") authorization: String
    ): UserJson {
        val userId = tokenService.readUserId(authorization)
        val user = userService.findUserById(userId)
        return user.toJson()
    }
}
```


```
class CoroutineDownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val data = downloadSynchronously()
        saveData(data)
        return Result.success()
    }
}
```


```
class UserProfileViewModel(
    private val loadProfileUseCase: LoadProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) {
    private val userProfile =
        MutableSharedFlow<UserProfileData>()

    val userName: Flow<String> = userProfile
        .map { it.name }
    val userSurname: Flow<String> = userProfile
        .map { it.surname }
    // ...

    fun onCreate() {
        viewModelScope.launch {
            val userProfileData =
                loadProfileUseCase.execute()
            userProfile.value = userProfileData
            // ...
        }
    }

    fun onNameChanged(newName: String) {
        viewModelScope.launch {
            val newProfile = userProfile.copy(name = newName)
            userProfile.value = newProfile
            updateProfileUseCase.execute(newProfile)
        }
    }
}
```


```
class NotificationsSender(
    private val client: NotificationsClient,
    private val notificationScope: CoroutineScope,
) {
    fun sendNotifications(
        notifications: List<Notification>
    ) {
        for (n in notifications) {
            notificationScope.launch {
                client.send(n)
            }
        }
    }
}
```


```
class LatestNewsViewModel(
    private val newsRepository: NewsRepository
) : BaseViewModel() {
    private val _uiState =
        MutableStateFlow<NewsState>(LoadingNews)
    val uiState: StateFlow<NewsState> = _uiState

    fun onCreate() {
        scope.launch {
            _uiState.value =
                NewsLoaded(newsRepository.getNews())
        }
    }
}
```


```
val analyticsScope = CoroutineScope(SupervisorJob())
```


```
// Android example with cancellation and exception handler
abstract class BaseViewModel : ViewModel() {
    private val _failure: MutableLiveData<Throwable> =
        MutableLiveData()
    val failure: LiveData<Throwable> = _failure

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            _failure.value = throwable
        }

    private val context =
        Dispatchers.Main + SupervisorJob() + exceptionHandler

    protected val scope = CoroutineScope(context)

    override fun onCleared() {
        context.cancelChildren()
    }
}
```


```
// Spring example with custom exception handler
@Configuration
class CoroutineScopeConfiguration {

    @Bean
    fun coroutineDispatcher(): CoroutineDispatcher =
        Dispatchers.Default

    @Bean
    fun exceptionHandler(): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            FirebaseCrashlytics.getInstance()
                .recordException(throwable)
        }

    @Bean
    fun coroutineScope(
        coroutineDispatcher: CoroutineDispatcher,
        exceptionHandler: CoroutineExceptionHandler,
    ) = CoroutineScope(
        SupervisorJob() +
            coroutineDispatcher +
            exceptionHandler
    )
}
```


```
//1
import kotlinx.coroutines.runBlocking
annotation class Test

fun main() = runBlocking {
    // ...
}

class SomeTests {
    @Test
    fun someTest() = runBlocking {
        // ...
    }
}
```


```
//2
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
annotation class Test

suspend fun main() = coroutineScope {
    // ...
}

class SomeTests {
    @Test
    fun someTest() = runTest {
        // ...
    }
}
```


```
class NotificationsSender(
    private val client: NotificationsClient,
    private val notificationScope: CoroutineScope,
) {
    @Measure
    fun sendNotifications(notifications: List<Notification>){
        val jobs = notifications.map { notification ->
            scope.launch {
                client.send(notification)
            }
        }
        // We block thread here until all notifications are
        // sent to make function execution measurement
        // give us correct execution time
        runBlocking { jobs.joinAll() }
    }
}
```


```
fun updateNews() {
    newsFlow()
        .onStart { showProgressBar() }
        .onCompletion { hideProgressBar() }
        .onEach { view.showNews(it) }
        .catch { view.handleError(it) }
        .launchIn(viewModelScope)
}
```


```
class NewsViewModel : BaseViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _news = MutableStateFlow(emptyList<News>())
    val news: StateFlow<List<News>> = _news

    fun onCreate() {
        newsFlow()
            .onStart { _loading.value = true }
            .onCompletion { _loading.value = false }
            .onEach { _news.value = it }
            .catch { _failure.value = it }
            .launchIn(viewModelScope)
    }
}

class LatestNewsActivity : AppCompatActivity() {
    @Inject
    val newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // ...
        launchOnStarted {
            newsViewModel.loading.collect {
                progressBar.visbility =
                    if (it) View.VISIBLE else View.GONE
            }
        }
        launchOnStarted {
            newsViewModel.news.collect {
                newsList.adapter = NewsAdapter(it)
            }
        }
    }
}
```


```
class NewsViewModel : BaseViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    val newsState: StateFlow<List<News>> = newsFlow()
        .onStart { _loading.value = true }
        .onCompletion { _loading.value = false }
        .catch { _failure.value = it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )
}
```


```
class LocationsViewModel(
    locationService: LocationService
) : ViewModel() {

    private val location = locationService.observeLocations()
        .map { it.toLocationsDisplay() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = LocationsDisplay.Loading,
        )

    // ...
}
```


```
class UserProfileViewModel {
    private val _userChanges =
        MutableSharedFlow<UserChange>()
    val userChanges: SharedFlow<UserChange> = _userChanges

    fun onCreate() {
        viewModelScope.launch {
            userChanges.collect(::applyUserChange)
        }
    }

    fun onNameChanged(newName: String) {
        // ...
        _userChanges.emit(NameChange(newName))
    }

    fun onPublicKeyChanged(newPublicKey: String) {
        // ...
        _userChanges.emit(PublicKeyChange(newPublicKey))
    }
}
```