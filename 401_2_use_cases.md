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
suspend fun produceCurrentUser(): User = coroutineScope {
    val profile = async { repo.getProfile() }
    val friends = async { repo.getFriends() }
    User(profile.await(), friends.await())
}
```


```
suspend fun produceCurrentUserSeq(): User {
    val profile = repo.getProfile()
    val friends = repo.getFriends()
    return User(profile, friends)
}

suspend fun produceCurrentUserPar(): User = coroutineScope {
    val profile = async { repo.getProfile() }
    val friends = async { repo.getFriends() }
    User(profile.await(), friends.await())
}
```


```
suspend fun produceCurrentUserPar(): User = coroutineScope {
    val profile = async { repo.getProfile() }
    val friends = repo.getFriends()
    User(profile.await(), friends)
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
        .awaitAll()
        .flatten()
}
```


```
fun getOffers(
    categories: List<Category>
): Flow<List<Offer>> = categories
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
suspend fun getUserOrNull(): User? =
    withTimeoutOrNull(5000) {
        fetchUser()
    }
```


```
class UserStateProvider(
    private val userRepository: UserRepository
) {

    fun userStateFlow(): Flow<User> = userRepository
        .observeUserChanges()
        .filter { it.isSignificantChange }
        .scan(userRepository.currentUser()) { user, update ->
            user.with(update)
        }
        .map { it.toDomainUser() }
}
```


```
class ArticlesProvider(
    private val ktAcademy: KtAcademyRepository,
    private val kotlinBlog: KtAcademyRepository,
) {
    fun observeArticles(): Flow<Article> = merge(
        ktAcademy.observeArticles().map { it.toArticle() },
        kotlinBlog.observeArticles().map { it.toArticle() },
    )
}

class NotificationStatusProvider(
    private val userStateProvider: UserStateProvider,
    private val notificationsProvider: NotificationsProvider,
    private val statusFactory: NotificationStatusFactory,
) {
    fun notificationStatusFlow(): NotificationStatus =
        notificationsProvider.observeNotifications()
            .filter { it.status == Notification.UNSEEN }
            .combine(userStateProvider.userStateFlow()) { 
                    notifications, user ->
                statusFactory.produce(notifications, user)
            }
}
```


```
class LocationService(
    locationDao: LocationDao,
    scope: CoroutineScope
) {
    private val locations = locationDao.observeLocations()
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
        )

    fun observeLocations(): Flow<List<Location>> = locations
}
```