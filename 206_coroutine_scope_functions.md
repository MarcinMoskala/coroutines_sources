```
// Data loaded sequentially, not simultaneously
suspend fun getUserProfile(): UserProfileData {
   val user = getUserData() // (1 sec)
   val notifications = getNotifications() // (1 sec)

   return UserProfileData(
       user = user,
       notifications = notifications,
   )
}
```


```
// DON'T DO THAT
suspend fun getUserProfile(): UserProfileData {
   val user = GlobalScope.async { getUserData() }
   val notifications = GlobalScope.async {
       getNotifications()
   }

   return UserProfileData(
       user = user.await(), // (1 sec)
       notifications = notifications.await(),
   )
}
```


```
public object GlobalScope : CoroutineScope {
   override val coroutineContext: CoroutineContext
       get() = EmptyCoroutineContext
}
```


```
// DON'T DO THAT
suspend fun getUserProfile(
   scope: CoroutineScope
): UserProfileData {
   val user = scope.async { getUserData() }
   val notifications = scope.async { getNotifications() }

   return UserProfileData(
       user = user.await(), // (1 sec)
       notifications = notifications.await(),
   )
}

// or

// DON'T DO THAT
suspend fun CoroutineScope.getUserProfile(): UserProfileData {
   val user = async { getUserData() }
   val notifications = async { getNotifications() }

   return UserProfileData(
       user = user.await(), // (1 sec)
       notifications = notifications.await(),
   )
}
```


```
//1
import kotlinx.coroutines.*

//sampleStart
data class Details(val name: String, val followers: Int)
data class Tweet(val text: String)

fun getFollowersNumber(): Int =
   throw Error("Service exception")

suspend fun getUserName(): String {
   delay(500)
   return "marcinmoskala"
}

suspend fun getTweets(): List<Tweet> {
   return listOf(Tweet("Hello, world"))
}

suspend fun CoroutineScope.getUserDetails(): Details {
   val userName = async { getUserName() }
   val followersNumber = async { getFollowersNumber() }
   return Details(userName.await(), followersNumber.await())
}

fun main() = runBlocking {
   val details = try {
       getUserDetails()
   } catch (e: Error) {
       null
   }
   val tweets = async { getTweets() }
   println("User: $details")
   println("Tweets: ${tweets.await()}")
}
// Only Exception...
//sampleEnd
```


```
suspend fun <R> coroutineScope(
   block: suspend CoroutineScope.() -> R
): R
```


```
//2
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

//sampleStart
fun main() = runBlocking {
   val a = coroutineScope {
       delay(1000)
       10
   }
   println("a is calculated")
   val b = coroutineScope {
       delay(1000)
       20
   }
   println(a) // 10
   println(b) // 20
}
// (1 sec)
// a is calculated
// (1 sec)
// 10
// 20
//sampleEnd
```


```
//3
import kotlinx.coroutines.*

//sampleStart
suspend fun longTask() = coroutineScope {
   launch {
       delay(1000)
       val name = coroutineContext[CoroutineName]?.name
       println("[$name] Finished task 1")
   }
   launch {
       delay(2000)
       val name = coroutineContext[CoroutineName]?.name
       println("[$name] Finished task 2")
   }
}

fun main() = runBlocking(CoroutineName("Parent")) {
   println("Before")
   longTask()
   println("After")
}
// Before
// (1 sec)
// [Parent] Finished task 1
// (1 sec)
// [Parent] Finished task 2
// After
//sampleEnd
```


```
//4
import kotlinx.coroutines.*

//sampleStart
suspend fun longTask() = coroutineScope {
   launch {
       delay(1000)
       val name = coroutineContext[CoroutineName]?.name
       println("[$name] Finished task 1")
   }
   launch {
       delay(2000)
       val name = coroutineContext[CoroutineName]?.name
       println("[$name] Finished task 2")
   }
}

fun main(): Unit = runBlocking {
   val job = launch(CoroutineName("Parent")) {
       longTask()
   }
   delay(1500)
   job.cancel()
}
// [Parent] Finished task 1
//sampleEnd
```


```
//5
import kotlinx.coroutines.*

//sampleStart
data class Details(val name: String, val followers: Int)
data class Tweet(val text: String)
class ApiException(
   val code: Int,
   message: String
) : Throwable(message)

fun getFollowersNumber(): Int =
   throw ApiException(500, "Service unavailable")

suspend fun getUserName(): String {
   delay(500)
   return "marcinmoskala"
}

suspend fun getTweets(): List<Tweet> {
   return listOf(Tweet("Hello, world"))
}

suspend fun getUserDetails(): Details = coroutineScope {
   val userName = async { getUserName() }
   val followersNumber = async { getFollowersNumber() }
   Details(userName.await(), followersNumber.await())
}

fun main() = runBlocking<Unit> {
   val details = try {
       getUserDetails()
   } catch (e: ApiException) {
       null
   }
   val tweets = async { getTweets() }
   println("User: $details")
   println("Tweets: ${tweets.await()}")
}
// User: null
// Tweets: [Tweet(text=Hello, world)]
//sampleEnd
```


```
suspend fun getUserProfile(): UserProfileData =
   coroutineScope {
       val user = async { getUserData() }
       val notifications = async { getNotifications() }

       UserProfileData(
           user = user.await(),
           notifications = notifications.await(),
       )
   }
```


```
//6
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
   launch {
       delay(1000)
       println("World")
   }
   println("Hello, ")
}
// Hello
// (1 sec)
// World
//sampleEnd
```


```
suspend fun produceCurrentUserSeq(): User {
    val profile = repo.getProfile()
    val friends = repo.getFriends()
    return User(profile, friends)
}

suspend fun produceCurrentUserSym(): User = coroutineScope {
    val profile = async { repo.getProfile() }
    val friends = async { repo.getFriends() }
    User(profile.await(), friends.await())
}
```


```
//7
import kotlinx.coroutines.*

//sampleStart
fun CoroutineScope.log(text: String) {
   val name = this.coroutineContext[CoroutineName]?.name
   println("[$name] $text")
}

fun main() = runBlocking(CoroutineName("Parent")) {
   log("Before")

   withContext(CoroutineName("Child 1")) {
       delay(1000)
       log("Hello 1")
   }

   withContext(CoroutineName("Child 2")) {
       delay(1000)
       log("Hello 2")
   }

   log("After")
}
// [Parent] Before
// (1 sec)
// [Child 1] Hello 1
// (1 sec)
// [Child 2] Hello 2
// [Parent] After
//sampleEnd
```


```
launch(Dispatchers.Main) {
   view.showProgressBar()
   withContext(Dispatchers.IO) {
       fileRepository.saveData(data)
   }
   view.hideProgressBar()
}
```


```
//8
import kotlinx.coroutines.*

//sampleStart
fun main() = runBlocking {
   println("Before")

   supervisorScope {
       launch {
           delay(1000)
           throw Error()
       }

       launch {
           delay(2000)
           println("Done")
       }
   }

   println("After")
}
// Before
// (1 sec)
// Exception...
// (1 sec)
// Done
// After
//sampleEnd
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
//9
import kotlinx.coroutines.*

//sampleStart
fun main() = runBlocking {
   println("Before")

   withContext(SupervisorJob()) {
       launch {
           delay(1000)
           throw Error()
       }

       launch {
           delay(2000)
           println("Done")
       }
   }

   println("After")
}
// Before
// (1 sec)
// Exception...
//sampleEnd
```


```
//10
import kotlinx.coroutines.*

suspend fun test(): Int = withTimeout(1500) {
    delay(1000)
    println("Still thinking")
    delay(1000)
    println("Done!")
    42
}

suspend fun main(): Unit = coroutineScope {
    try {
        test()
    } catch (e: TimeoutCancellationException) {
        println("Cancelled")
    }
    delay(1000) // Extra timeout does not help,
    // `test` body was cancelled
}
// (1 sec)
// Still thinking
// (0.5 sec)
// Cancelled
```


```
// will not start, because runTest requires kotlinx-coroutines-test, but you can copy it to your project
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

class Test {
    @Test
    fun testTime2() = runTest {
        withTimeout(1000) {
            // something that should take less than 1000
            delay(900) // virtual time
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun testTime1() = runTest {
        withTimeout(1000) {
            // something that should take more than 1000
            delay(1100) // virtual time
        }
    }

    @Test
    fun testTime3() = runBlocking {
        withTimeout(1000) {
            // normal test, that should not take too long
            delay(900) // really waiting 900 ms
        }
    }
}
```


```
//11
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
   launch { // 1
       launch { // 2, cancelled by its parent
           delay(2000)
           println("Will not be printed")
       }
       withTimeout(1000) { // we cancel launch
           delay(1500)
       }
   }
   launch { // 3
       delay(2000)
       println("Done")
   }
}
// (2 sec)
// Done
```


```
//12
import kotlinx.coroutines.*

class User()

suspend fun fetchUser(): User {
    // Runs forever
    while (true) {
        yield()
    }
}

suspend fun getUserOrNull(): User? =
    withTimeoutOrNull(5000) {
        fetchUser()
    }

suspend fun main(): Unit = coroutineScope {
    val user = getUserOrNull()
    println("User: $user")
}
// (5 sec)
// User: null
```


```
suspend fun calculateAnswerOrNull(): User? =
   withContext(Dispatchers.Default) {
       withTimeoutOrNull(1000) {
           calculateAnswer()
       }
   }
```


```
class ShowUserDataUseCase(
    private val repo: UserDataRepository,
    private val view: UserDataView,
) {

    suspend fun showUserData() = coroutineScope {
        val name = async { repo.getName() }
        val friends = async { repo.getFriends() }
        val profile = async { repo.getProfile() }
        val user = User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await()
        )
        view.show(user)
        launch { repo.notifyProfileShown() }
    }
}
```


```
fun onCreate() {
   viewModelScope.launch {
       _progressBar.value = true
       showUserData()
       _progressBar.value = false
   }
}
```


```
val analyticsScope = CoroutineScope(SupervisorJob())
```


```
class ShowUserDataUseCase(
   private val repo: UserDataRepository,
   private val view: UserDataView,
   private val analyticsScope: CoroutineScope,
) {

   suspend fun showUserData() = coroutineScope {
       val name = async { repo.getName() }
       val friends = async { repo.getFriends() }
       val profile = async { repo.getProfile() }
       val user = User(
           name = name.await(),
           friends = friends.await(),
           profile = profile.await()
       )
       view.show(user)
       analyticsScope.launch { repo.notifyProfileShown() }
   }
}
```