```
//1
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope

suspend fun main(): Unit = coroutineScope {
    val job = launch {
        repeat(1_000) { i ->
            delay(200)
            println("Printing $i")
        }
    }

    delay(1100)
    job.cancel()
    job.join()
    println("Cancelled successfully")
}
// (0.2 sec)
// Printing 0
// (0.2 sec)
// Printing 1
// (0.2 sec)
// Printing 2
// (0.2 sec)
// Printing 3
// (0.2 sec)
// Printing 4
// (0.1 sec)
// Cancelled successfully
```


```
//2
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = launch {
        try {
            repeat(1_000) { i ->
                delay(200)
                println("Printing $i")
            }
        } catch (e: CancellationException) {
            println("Cancelled with $e")
            throw e
        } finally {
            println("Finally")
        }
    }
    delay(700)
    job.cancel()
    job.join()
    println("Cancelled successfully")
    delay(1000)
}
// (0.2 sec)
// Printing 0
// (0.2 sec)
// Printing 1
// (0.2 sec)
// Printing 2
// (0.1 sec)
// Cancelled with JobCancellationException...
// Finally
// Cancelled successfully
```


```
public suspend fun Job.cancelAndJoin() {
  cancel()
  return join()
}
```


```
//3
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = launch {
        repeat(1_000) { i ->
            delay(200)
            println("Printing $i")
        }
    }
    job.invokeOnCompletion {
        if (it is CancellationException) {
            println("Cancelled with $it")
        }
        println("Finally")
    }
    delay(700)
    job.cancel()
    job.join()
    println("Cancelled successfully")
    delay(1000)
}
// (0.2 sec)
// Printing 0
// (0.2 sec)
// Printing 1
// (0.2 sec)
// Printing 2
// (0.1 sec)
// Cancelled with JobCancellationException...
// Finally
// Cancelled successfully
```


```
//4
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    var childJob: Job? = null
    val job = launch {
        launch {
            try {
                delay(1000)
                println("A")
            } finally {
                println("A finished")
            }
        }
        childJob = launch {
            try {
                delay(2000)
                println("B")
            } catch (e: CancellationException) {
                println("B cancelled")
            }
        }
        launch {
            delay(3000)
            println("C")
        }.invokeOnCompletion {
            println("C finished")
        }
    }
    delay(100)
    job.cancel()
    job.join()
    println("Cancelled successfully")
    println(childJob?.isCancelled)
}
// (0.1 sec)
// (the below order might be different)
// A finished
// B cancelled
// C finished
// Cancelled successfully
// true
```


```
fun CoroutineScope(
    context: CoroutineContext
): CoroutineScope = ContextScope(
    if (context[Job] != null) context else context + Job()
)
```


```
fun CoroutineScope.cancel(cause: CancellationException? = null) {
    val job = coroutineContext[Job] ?: error("...")
    job.cancel(cause)
}
```


```
class OfferUploader {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun upload(offer: Offer) {
        scope.launch {
            // upload
        }
    }

    fun cancel() {
        scope.cancel()
    }
}
```


```
//5
import kotlinx.coroutines.*

suspend fun main() {
    val scope = CoroutineScope(Job())
    scope.cancel()
    val job = scope.launch { // will be ignored
        println("Will not be printed")
    }
    job.join()
}
```


```
class ProfileViewModel : ViewModel() {
    private val scope =
        CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun onCreate() {
        scope.launch { loadUserData() }
    }

    override fun onCleared() {
        scope.coroutineContext.cancelChildren()
    }

    // ...
}
```


```
//6
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            println("Coroutine started")
            delay(200)
            println("Coroutine finished")
        } finally {
            println("Finally")
            launch {
                println("Children executed")
            }
            delay(1000L)
            println("Cleanup done")
        }
    }
    delay(100)
    job.cancelAndJoin()
    println("Done")
}

// Coroutine started
// (0.1 sec)
// Finally
// Done
```


```
//7
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            println("Coroutine started")
            delay(200)
            println("Coroutine finished")
        } finally {
            println("Finally")
            withContext(NonCancellable) {
                launch {
                    println("Children executed")
                }
                delay(1000L)
                println("Cleanup done")
            }
        }
    }
    delay(100)
    job.cancelAndJoin()
    println("Done")
}
// Coroutine started
// (0.1 sec)
// Finally
// Children executed
// (1 sec)
// Cleanup done
// Done
```


```
suspend fun operation() {
    try {
        // operation
    } finally {
        withContext(NonCancellable) {
            // cleanup that requires suspending call
        }
    }
}
```


```
//8
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        repeat(1_000) { i ->
            Thread.sleep(200) // We might have some
            // complex operations or reading files here
            println("Printing $i")
        }
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancelled successfully")
    delay(1000)
}
// Printing 0
// Printing 1
// Printing 2
// ... (up to 1000)
```


```
//9
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        repeat(1_000) { i ->
            Thread.sleep(200)
            yield()
            println("Printing $i")
        }
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
    delay(1000)
}
// Printing 0
// Printing 1
// Printing 2
// Printing 3
// Printing 4
// Cancelled successfully
```


```
suspend fun cpu() = withContext(Dispatchers.Default) {
    cpuIntensiveOperation1()
    yield()
    cpuIntensiveOperation2()
    yield()
    cpuIntensiveOperation3()
}
```


```
public val CoroutineScope.isActive: Boolean
  get() = coroutineContext[Job]?.isActive ?: true
```


```
//10
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        do {
            Thread.sleep(200)
            println("Printing")
        } while (isActive)
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
}
// Printing
// Printing
// Printing
// Printing
// Printing
// Printing
// Cancelled successfully
```


```
//11
import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        repeat(1000) { num ->
            Thread.sleep(200)
            ensureActive()
            println("Printing $num")
        }
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
}
// Printing 0
// Printing 1
// Printing 2
// Printing 3
// Printing 4
// Cancelled successfully
```


```
suspend fun operation() {
    try {
        // suspending operation
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // ignore other exceptions
    }
}
```


```
suspend fun operation() {
    try {
        // suspending operation
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        // ignore other exceptions
    }
}
```


```
suspend fun operation() {
    try {
        // suspending operation
    } catch (e: CancellationException) {
        // do something
        throw e
    }
}
```


```
//12
import kotlinx.coroutines.*

class MyNonPropagatingException : CancellationException()

suspend fun main(): Unit = coroutineScope {
  launch { // 1
      launch { // 2
          delay(2000)
          println("Will not be printed")
      }
      delay(1000)
      throw MyNonPropagatingException() // 3
  }
  launch { // 4
      delay(2000)
      println("Will be printed")
  }
}
// (2 sec)
// Will be printed
```


```
//13
import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

// Poor practice, do not do this
class UserNotFoundException : CancellationException()

suspend fun main() {
    try {
        updateUserData()
    } catch (e: UserNotFoundException) {
        println("User not found")
    }
}

suspend fun updateUserData() {
    updateUser()
    updateTweets()
}
suspend fun updateTweets() { 
    delay(1000)
    println("Updating...") 
}
suspend fun updateUser() { throw UserNotFoundException() }
// User not found
```


```
//14
import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

// Poor practice, do not do this
class UserNotFoundException : CancellationException()

suspend fun main() {
    try {
        updateUserData()
    } catch (e: UserNotFoundException) {
        println("User not found")
    }
}

suspend fun updateUserData() = coroutineScope {
    launch { updateUser() }
    launch { updateTweets() }
}
suspend fun updateTweets() { 
    delay(1000)
    println("Updating...") 
}
suspend fun updateUser() { throw UserNotFoundException() }
// (1 sec)
// Updating...
```


```
//15
import kotlinx.coroutines.*

class UserNotFoundException : RuntimeException()

suspend fun main() {
    try {
        updateUserData()
    } catch (e: UserNotFoundException) {
        println("User not found")
    }
}

suspend fun updateUserData() {
    updateUser()
    updateTweets()
}
suspend fun updateTweets() { 
    delay(1000)
    println("Updating...") 
}
suspend fun updateUser() { throw UserNotFoundException() }
// User not found
```


```
//16
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
//17
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
//18
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
suspend fun someTask() = suspendCancellableCoroutine { cont ->
    // rest of the implementation
    cont.invokeOnCancellation { /* cleanup */ }
}
```