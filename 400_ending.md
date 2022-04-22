```
// Retrofit
class GithubApi {
   @GET("orgs/{organization}/repos?per_page=100")
   suspend fun getOrganizationRepos(
       @Path("organization") organization: String
   ): List<Repo>
}

// Room
@Dao
interface LocationDao {
   @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun insertLocation(location: Location)

   @Query("DELETE FROM location_table")
   suspend fun deleteLocations()

   @Query("SELECT * FROM location_table ORDER BY time")
   fun observeLocations(): Flow<List<Location>>
}
```


```
class DiscUserRepository(
   private val discReader: DiscReader
) : UserRepository {
   private val dispatcher = Dispatchers.IO
       .limitedParallelism(5)

   override suspend fun getUser(): UserData =
       withContext(dispatcher) {
           discReader.read<UserData>("userName")
       }
}
```


```
suspend fun requestNews(): News {
  return suspendCancellableCoroutine<News> { cont ->
      val call = requestNews(
          onSuccess = { news -> cont.resume(news) },
          onError = { e -> cont.resumeWithException(e) }
      )
      cont.invokeOnCancellation {
          call.cancel()
      }
  }
}
```


```
suspend fun calculateModel() =
   withContext(Dispatchers.Default) {
       model.fit(
           dataset = newTrain,
           epochs = 10,
           batchSize = 100,
           verbose = false
       )
   }

suspend fun setUserName(name: String) =
   withContext(Dispatchers.Main.immediate) {
       userNameView.text = name
   }
```


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


```
// On any platform
val analyticsScope = CoroutineScope(SupervisorJob())

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

// Spring example with custom exception handler
@Configuration
public class CoroutineScopeConfiguration {

   @Bean(name = "coroutineDispatcher")
   fun coroutineDispatcher(): CoroutineDispatcher =
       Dispatchers.IO.limitedParallelism(5)

   @Bean(name = "coroutineExceptionHandler")
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
               coroutineExceptionHandler
   )
}
```


```
class NotificationsSender(
   private val client: NotificationsClient,
   private val exceptionCollector: ExceptionCollector,
) {
   private val handler = CoroutineExceptionHandler { _, e ->
       exceptionCollector.collectException(e)
   }
   private val job = SupervisorJob()
   private val scope = CoroutineScope(job + handler)

   fun sendNotifications(notifications: List<Notification>) {
       val jobs = notifications.map { notification ->
           scope.launch {
               client.send(notification)
           }
       }
       runBlocking { jobs.joinAll() }
   }

   fun cancel() {
       job.cancelChildren()
   }
}
```


```
class NetworkUserRepository(
    private val api: UserApi,
) : UserRepository {
    override suspend fun getUser(): User =
        api.getUser().toDomainUser()
}

class NetworkNewsService(
    private val newsRepo: NewsRepository,
    private val settings: SettingsRepository,
) {

    suspend fun getNews(): List<News> = newsRepo
        .getNews()
        .map { it.toDomainNews() }

    suspend fun getNewsSummary(): List<News> {
        val type = settings.getNewsSummaryType()
        return newsRepo.getNewsSummary(type)
    }
}
```


```
suspend fun getArticlesForUser(
   userToken: String?,
): List<ArticleJson> = coroutineScope {
   val articles = async { articleRepository.getArticles() }
   val user = userService.getUser(userToken)
   articles.await()
       .filter { canSeeOnList(user, it) }
       .map { toArticleJson(it) }
}
```


```
suspend fun getOffers(
   categories: List<Category>
): List<Offer> = coroutineScope {
   categories
       .map { async { api.requestOffers(it) } }
       .flatMap { it.await() }
}

// A better solution
suspend fun getOffers(
   categories: List<Category>
): Flow<Offer> = categories
    .asFlow()
    .flatMapMerge(concurrency = 20) {
        suspend { api.requestOffers(it) }.asFlow()
        // or flow { emit(api.requestOffers(it)) }
    }
```


```
suspend fun notifyAnalytics(actions: List<UserAction>) =
   supervisorScope {
       actions.forEach { action ->
           launch {
               notifyAnalytics(action)
           }
       }
   }
```


```
class ArticlesRepositoryComposite(
   private val articleRepositories: List<ArticleRepository>,
) : ArticleRepository {
   override suspend fun fetchArticles(): List<Article> =
       supervisorScope {
           articleRepositories
               .map { async { it.fetchArticles() } }
               .mapNotNull {
                   try {
                       it.await()
                   } catch (e: Throwable) {
                       e.printStackTrace()
                       null
                   }
               }
               .flatten()
               .sortedByDescending { it.publishedAt }
       }
}
```


```
suspend fun getUserOrNull(): User? =
   withTimeoutOrNull(5000) {
       fetchUser()
   }
```