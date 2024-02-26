```
// Don't
suspend fun getUser(): User = coroutineScope {
    val user = async { repo.getUser() }.await()
    user.toUser()
}

// Do
suspend fun getUser(): User {
    val user = repo.getUser()
    return user.toUser()
}
```


```
fun showNews() {
    viewModelScope.launch {
        val config = async { getConfigFromApi() }
        val news = async { getNewsFromApi(config.await()) }
        val user = async { getUserFromApi() } // async not
        // necessary here, but useful for readability
        view.showNews(user.await(), news.await())
    }
}
```


```
class DiscSaveRepository(
    private val discReader: DiscReader
) : SaveRepository {
    
    override suspend fun loadSave(name: String): SaveData =
        withContext(Dispatchers.IO) {
            discReader.read("save/$name")
        }
}
```


```
class DiscSaveRepository(
    private val discReader: DiscReader,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) : SaveRepository {
    
    override suspend fun loadSave(name: String): SaveData =
        withContext(dispatcher) {
            discReader.read("save/$name")
        }
}
```


```
suspend fun showUser(user: User) =
    withContext(Dispatchers.Main.immediate) {
        userNameElement.text = user.name
        // ...
    }
```


```
suspend fun cpuIntensiveOperations() =
    withContext(Dispatchers.Default) {
        cpuIntensiveOperation1()
        yield()
        cpuIntensiveOperation2()
        yield()
        cpuIntensiveOperation3()
    }

suspend fun blockingOperations() =
    withContext(Dispatchers.IO) {
        blockingOperation1()
        yield()
        blockingOperation2()
        yield()
        blockingOperation3()
    }
```


```
//1
import kotlinx.coroutines.*

//sampleStart
suspend fun longTask() = coroutineScope {
    launch {
        delay(1000)
        println("Done 1")
    }
    launch {
        delay(2000)
        println("Done 2")
    }
}

suspend fun main() {
    println("Before")
    longTask()
    println("After")
}
// Before
// (1 sec)
// Done 1
// (1 sec)
// Done 2
// After
//sampleEnd
```


```
suspend fun updateUser() = coroutineScope {
    // ...
    
    // Don't
    launch { sendEvent(UserSunchronized) }
}
```


```
suspend fun updateUser() = coroutineScope {
    // ...
    
    eventsScope.launch { sendEvent(UserSunchronized) }
}
```


```
// Don't
fun main() = runBlocking(SupervisorJob()) {
    launch {
        delay(1000)
        throw Error()
    }
    launch {
        delay(2000)
        println("Done")
    }
    launch {
        delay(3000)
        println("Done")
    }
}
// (1 sec)
// Error...
```


```
// Don't
suspend fun sendNotifications(
    notifications: List<Notification>
) = withContext(SupervisorJob()) {
    for (notification in notifications) {
        launch {
            client.send(notification)
        }
    }
}
```


```
// Do
suspend fun sendNotifications(
    notifications: List<Notification>
) = supervisorScope {
    for (notification in notifications) {
        launch {
            client.send(notification)
        }
    }
}
```


```
// Don't
suspend fun getPosts() = withContext(Job()) {
    val user = async { userService.currentUser() }
    val posts = async { postsService.getAll() }
    posts.await()
        .filterCanSee(user.await())
}
```


```
// Don't
val scope = CoroutineScope(Job())

// Do
val scope = CoroutineScope(SupervisorJob())
```


```
fun onCleared() {
    // Consider doing
    scope.coroutineContext.cancelChildren()
    
    // Instead of
    scope.cancel()
}
```


```
class MainViewModel : ViewModel() {
    val scope = CoroutineScope(SupervisorJob())
    
    fun onCreate() {
        viewModelScope.launch {
            // Will be cancelled with MainViewModel
            launch { task1() }
            // Will never be cancelled
            GlobalScope.launch { task2() }
            // Will be cancelled when we cancel scope
            scope.launch { task2() }
        }
    }
}
```


```
val scope = CoroutineScope(SupervisorJob())

fun example() {
    // Don't
    GlobalScope.launch { task() }
    
    // Do
    scope.launch { task() }
}
```


```
// GlobalScope definition
public object GlobalScope : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}
```


```
//2
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        delay(1000)
        println("Text 1")
    }
    launch(job) {
        delay(2000)
        println("Text 2")
    }
    job.join() // Here we will await forever
    println("Will not be printed")
}
// (1 sec)
// Text 1
// (1 sec)
// Text 2
// (runs forever)
//sampleEnd
```


```
class SomeService {
    private var job: Job? = null
    private val scope = CoroutineScope(SupervisorJob())
    
    // Every time we start a new task,
    // we cancel the previous one.
    fun startTask() {
        cancelTask()
        job = scope.launch {
            // ...
        }
    }
    
    fun cancelTask() {
        job?.cancel()
    }
}
```


```
class SomeService {
    private var jobs: List<Job> = emptyList()
    private val scope = CoroutineScope(SupervisorJob())
    
    fun startTask() {
        jobs += scope.launch {
            // ...
        }
    }
    
    fun cancelTask() {
        jobs.forEach { it.cancel() }
    }
}
```


```
// Don’t use suspending functions returning Flow
suspend fun observeNewsServices(): Flow<News> {
    val newsServices = fetchNewsServices()
    return newsServices
        .asFlow()
        .flatMapMerge { it.observe() }
}

suspend fun main() {
    val flow = observeNewsServices() // Fetching services
    // ...
    flow.collect { println(it) } // Start observing
}
```


```
fun observeNewsServices(): Flow<News> {
    return flow { emitAll(fetchNewsServices().asFlow()) }
        .flatMapMerge { it.observe() }
}

suspend fun main() {
    val flow = observeNewsServices()
    // ...
    flow.collect { println(it) }
    // Fetching services
    // Start observing
}
```


```
suspend fun fetchNewsFromServices(): List<News> {
    return fetchNewsServices()
        .mapAsync { it.observe() }
        .flatten()
}

suspend fun main() {
    val news = fetchNewsFromServices()
    // Fetching services
    // Start observing
    // ...
}
```


```
interface UserRepository {
    fun getUser(): Flow<User>
}
```


```
interface UserRepository {
    suspend fun getUser(): User
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