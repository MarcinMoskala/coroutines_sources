```
class GithubApi {
   @GET("orgs/{organization}/repos?per_page=100")
   suspend fun getOrganizationRepos(
       @Path("organization") organization: String
   ): List<Repo>
}

class GithubConnectorService(
    private val githubApi: GithubApi
) {
    suspend fun getKotlinRepos() = 
        githubApi.getOrganizationRepos("kotlin")
            .map { it.toDomain() }
}

@Controller
class UserController(
   private val githubConnectorService: GithubConnectorService,
) {
   @GetMapping("/kotlin/repos")
    suspend fun findUser(): GithubReposResponseJson = 
       githubConnectorService.getKotlinRepos().toJson()
}
```


```
//1
import kotlinx.coroutines.*

// Suspending function can suspend a coroutine
suspend fun a() {
    // Suspends the coroutine for 1 second
    delay(1000)
    println("A")
}

// Suspending main is started by Kotlin in a coroutine
suspend fun main() {
    println("Before")
    a()
    println("After")
}
// Before
// (1 second delay)
// A
// After
```


```
//2
import kotlin.coroutines.*

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { }

    println("After")
}
// Before
```


```
//3
import kotlin.coroutines.*

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        println("Before too")
        continuation.resume(Unit)
    }

    println("After")
}
// Before
// Before too
// After
```


```
inline fun <T> Continuation<T>.resume(value: T): Unit =
    resumeWith(Result.success(value))

inline fun <T> Continuation<T>.resumeWithException(
    exception: Throwable
): Unit = resumeWith(Result.failure(exception))
```


```
suspend fun a() {
    val a = "ABC"
    suspendCancellableCoroutine<Unit> { continuation ->
        // What is stored in the continuation?
        continuation.resume(Unit)
    }
    println(a)
}

suspend fun main() {
    val list = listOf(1, 2, 3)
    val text = "Some text"
    println(text)
    delay(1000)
    a()
    println(list)
}
```


```
//4
import kotlin.concurrent.thread
import kotlin.coroutines.*

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        thread {
            println("Suspended")
            Thread.sleep(1000)
            continuation.resume(Unit)
            println("Resumed")
        }
    }

    println("After")
}
// Before
// Suspended
// (1 second delay)
// After
// Resumed
```


```
//5
import kotlin.concurrent.thread
import kotlin.coroutines.*

fun continueAfterSecond(continuation: Continuation<Unit>) {
    thread {
        Thread.sleep(1000)
        continuation.resume(Unit)
    }
}

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        continueAfterSecond(continuation)
    }

    println("After")
}
// Before
// (1 sec)
// After
```


```
//6
import java.util.concurrent.*
import kotlin.coroutines.*

private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun main() {
    println("Before")

    suspendCancellableCoroutine<Unit> { continuation ->
        executor.schedule({
            continuation.resume(Unit)
        }, 1000, TimeUnit.MILLISECONDS)
    }

    println("After")
}
// Before
// (1 second delay)
// After
```


```
//7
import java.util.concurrent.*
import kotlin.coroutines.*

private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun delay(timeMillis: Long): Unit =
    suspendCancellableCoroutine { cont ->
        executor.schedule({
            cont.resume(Unit)
        }, timeMillis, TimeUnit.MILLISECONDS)
    }

suspend fun main() {
    println("Before")

    delay(1000)

    println("After")
}
// Before
// (1 second delay)
// After
```


```
val ret: Unit =
    suspendCancellableCoroutine<Unit> { cont: Continuation<Unit> ->
        cont.resume(Unit)
    }
```


```
//8
import kotlin.coroutines.*

suspend fun main() {
    val i: Int = suspendCancellableCoroutine<Int> { cont ->
        cont.resume(42)
    }
    println(i) // 42

    val str: String = suspendCancellableCoroutine<String> { cont ->
        cont.resume("Some text")
    }
    println(str) // Some text

    val b: Boolean = suspendCancellableCoroutine<Boolean> { cont ->
        cont.resume(true)
    }
    println(b) // true
}
```


```
//9
import kotlin.concurrent.thread
import kotlin.coroutines.*

data class User(val name: String)

fun requestUser(callback: (User) -> Unit) {
    thread {
        Thread.sleep(1000)
        callback.invoke(User("Test"))
    }
}

suspend fun main() {
    println("Before")
    val user = suspendCancellableCoroutine<User> { cont ->
        requestUser { user ->
            cont.resume(user)
        }
    }
    println(user)
    println("After")
}
// Before
// (1 second delay)
// User(name=Test)
// After
```


```
//10
import kotlin.concurrent.thread
import kotlin.coroutines.*

data class User(val name: String)

fun requestUser(callback: (User) -> Unit) {
    thread {
        Thread.sleep(1000)
        callback.invoke(User("Test"))
    }
}

suspend fun requestUser(): User {
    return suspendCancellableCoroutine<User> { cont ->
        requestUser { user ->
            cont.resume(user)
        }
    }
}

suspend fun main() {
    println("Before")
    val user = requestUser()
    println(user)
    println("After")
}
```


```
//11
import kotlin.coroutines.*

class MyException : Throwable("Just an exception")

suspend fun main() {
    try {
        suspendCancellableCoroutine<Unit> { cont ->
            cont.resumeWithException(MyException())
        }
    } catch (e: MyException) {
        println("Caught!")
    }
}
// Caught!
```


```
suspend fun requestUser(): User {
    return suspendCancellableCoroutine<User> { cont ->
        requestUser { resp ->
            if (resp.isSuccessful) {
                cont.resume(resp.data)
            } else {
                val e = ApiException(
                    resp.code,
                    resp.message
                )
                cont.resumeWithException(e)
            }
        }
    }
}

suspend fun requestNews(): News {
    return suspendCancellableCoroutine<News> { cont ->
        requestNews(
            onSuccess = { news -> cont.resume(news) },
            onError = { e -> cont.resumeWithException(e) }
        )
    }
}
```


```
//12
import kotlin.coroutines.*

// Do not do this
var continuation: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation() {
    suspendCancellableCoroutine<Unit> { cont ->
        continuation = cont
    }
}

suspend fun main() {
    println("Before")

    suspendAndSetContinuation()
    continuation?.resume(Unit)

    println("After")
}
// Before
```


```
//13
import kotlinx.coroutines.*
import kotlin.coroutines.*

// Do not do this, potential memory leak
var continuation: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation() {
    suspendCancellableCoroutine<Unit> { cont ->
       continuation = cont
   }
}

suspend fun main() = coroutineScope {
   println("Before")

   launch {
       delay(1000)
       continuation?.resume(Unit)
   }

   suspendAndSetContinuation()
   println("After")
}
// Before
// (1 second delay)
// After
```