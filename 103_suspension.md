```
//1
import kotlin.*

//sampleStart
suspend fun main() {
    println("Before")

    println("After")
}
// Before
// After
//sampleEnd
```


```
//2
import kotlin.coroutines.*

//sampleStart
suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { }

    println("After")
}
// Before
//sampleEnd
```


```
//3
import kotlin.coroutines.*

//sampleStart
suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        println("Before too")
    }

    println("After")
}
// Before
// Before too
//sampleEnd
```


```
//4
import kotlin.coroutines.*

//sampleStart
suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        continuation.resume(Unit)
    }

    println("After")
}
// Before
// After
//sampleEnd
```


```
inline fun <T> Continuation<T>.resume(value: T): Unit =
    resumeWith(Result.success(value))

inline fun <T> Continuation<T>.resumeWithException(
    exception: Throwable
): Unit = resumeWith(Result.failure(exception))
```


```
//5
import kotlin.concurrent.thread
import kotlin.coroutines.*

//sampleStart
suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
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
//sampleEnd
```


```
//6
import kotlin.concurrent.thread
import kotlin.coroutines.*

//sampleStart
fun continueAfterSecond(continuation: Continuation<Unit>) {
    thread {
        Thread.sleep(1000)
        continuation.resume(Unit)
    }
}

suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        continueAfterSecond(continuation)
    }

    println("After")
}
// Before
// (1 sec)
// After
//sampleEnd
```


```
//7
import java.util.concurrent.*
import kotlin.coroutines.*

//sampleStart
private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun main() {
    println("Before")

    suspendCoroutine<Unit> { continuation ->
        executor.schedule({
            continuation.resume(Unit)
        }, 1000, TimeUnit.MILLISECONDS)
    }

    println("After")
}
// Before
// (1 second delay)
// After
//sampleEnd
```


```
//8
import java.util.concurrent.*
import kotlin.coroutines.*

//sampleStart
private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun delay(timeMillis: Long): Unit =
    suspendCoroutine { cont ->
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
//sampleEnd
```


```
val ret: Unit =
    suspendCoroutine<Unit> { cont: Continuation<Unit> ->
        cont.resume(Unit)
    }
```


```
//9
import kotlin.coroutines.*

//sampleStart
suspend fun main() {
    val i: Int = suspendCoroutine<Int> { cont ->
        cont.resume(42)
    }
    println(i) // 42

    val str: String = suspendCoroutine<String> { cont ->
        cont.resume("Some text")
    }
    println(str) // Some text

    val b: Boolean = suspendCoroutine<Boolean> { cont ->
        cont.resume(true)
    }
    println(b) // true
}
//sampleEnd
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

//sampleStart
suspend fun main() {
    println("Before")
    val user = suspendCoroutine<User> { cont ->
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
//sampleEnd
```


```
//11
import kotlin.concurrent.thread
import kotlin.coroutines.*

data class User(val name: String)

fun requestUser(callback: (User) -> Unit) {
    thread {
        Thread.sleep(1000)
        callback.invoke(User("Test"))
    }
}

//sampleStart
suspend fun requestUser(): User {
    return suspendCoroutine<User> { cont ->
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
//sampleEnd
```


```
suspend fun requestUser(): User {
    return suspendCancellableCoroutine<User> { cont ->
        requestUser { user ->
            cont.resume(user)
        }
    }
}
```


```
//12
import kotlin.coroutines.*

//sampleStart
class MyException : Throwable("Just an exception")

suspend fun main() {
    try {
        suspendCoroutine<Unit> { cont ->
            cont.resumeWithException(MyException())
        }
    } catch (e: MyException) {
        println("Caught!")
    }
}
// Caught!
//sampleEnd
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
//13
import kotlin.coroutines.*

//sampleStart
// Do not do this
var continuation: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation() {
    suspendCoroutine<Unit> { cont ->
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
//sampleEnd
```


```
//14
import kotlinx.coroutines.*
import kotlin.coroutines.*

//sampleStart
// Do not do this, potential memory leak
var continuation: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation() {
   suspendCoroutine<Unit> { cont ->
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
//sampleEnd
```