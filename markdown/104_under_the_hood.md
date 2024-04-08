```
suspend fun getUser(): User?
suspend fun setUser(user: User)
suspend fun checkAvailability(flight: Flight): Boolean

// under the hood is
fun getUser(continuation: Continuation<*>): Any?
fun setUser(user: User, continuation: Continuation<*>): Any
fun checkAvailability(
  flight: Flight,
  continuation: Continuation<*>
): Any
```


```
suspend fun myFunction() {
  println("Before")
  delay(1000) // suspending
  println("After")
}
```


```
fun myFunction(continuation: Continuation<*>): Any
```


```
val continuation = MyFunctionContinuation(continuation)
```


```
val continuation =
  if (continuation is MyFunctionContinuation) continuation
  else MyFunctionContinuation(continuation)
```


```
val continuation = continuation as? MyFunctionContinuation
  ?: MyFunctionContinuation(continuation)
```


```
suspend fun myFunction() {
  println("Before")
  delay(1000) // suspending
  println("After")
}
```


```
// A simplified picture of how myFunction looks under the hood
fun myFunction(continuation: Continuation<Unit>): Any {
    val continuation = continuation as? MyFunctionContinuation
        ?: MyFunctionContinuation(continuation)

    if (continuation.label == 0) {
        println("Before")
        continuation.label = 1
        if (delay(1000, continuation) == COROUTINE_SUSPENDED){
            return COROUTINE_SUSPENDED
        }
    }
    if (continuation.label == 1) {
        println("After")
        return Unit
    }
    error("Impossible")
}
```


```
cont = object : ContinuationImpl(continuation) {
    var result: Any? = null
    var label = 0

    override fun invokeSuspend(`$result`: Any?): Any? {
        this.result = `$result`;
        return myFunction(this);
    }
};
```


```
//1
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

fun myFunction(continuation: Continuation<Unit>): Any {
    val continuation = continuation as? MyFunctionContinuation
        ?: MyFunctionContinuation(continuation)

    if (continuation.label == 0) {
        println("Before")
        continuation.label = 1
        if (delay(1000, continuation) == COROUTINE_SUSPENDED){
            return COROUTINE_SUSPENDED
        }
    }
    if (continuation.label == 1) {
        println("After")
        return Unit
    }
    error("Impossible")
}

class MyFunctionContinuation(
    val completion: Continuation<Unit>
) : Continuation<Unit> {
    override val context: CoroutineContext
        get() = completion.context

    var label = 0
    var result: Result<Any>? = null

    override fun resumeWith(result: Result<Unit>) {
        this.result = result
        val res = try {
            val r = myFunction(this)
            if (r == COROUTINE_SUSPENDED) return
            Result.success(r as Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
        completion.resumeWith(res)
    }
}


private val executor = Executors
    .newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

fun delay(timeMillis: Long, continuation: Continuation<Unit>): Any {
    executor.schedule({
        continuation.resume(Unit)
    }, timeMillis, TimeUnit.MILLISECONDS)
    return COROUTINE_SUSPENDED
}

fun main() {
    val EMPTY_CONTINUATION = object : Continuation<Unit> {
        override val context: CoroutineContext =
            EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) {
            // This is root coroutine, we don't need anything in this example
        }
    }
    myFunction(EMPTY_CONTINUATION)
    Thread.sleep(2000)
    // Needed to don't let the main finish immediately.
}

val COROUTINE_SUSPENDED = Any()
```


```
suspend fun myFunction() {
  println("Before")
  var counter = 0
  delay(1000) // suspending
  counter++
  println("Counter: $counter")
  println("After")
}
```


```
//2
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.*

fun myFunction(continuation: Continuation<Unit>): Any {
    val continuation = continuation as? MyFunctionContinuation
        ?: MyFunctionContinuation(continuation)

    var counter = continuation.counter

    if (continuation.label == 0) {
        println("Before")
        counter = 0
        continuation.counter = counter
        continuation.label = 1
        if (delay(1000, continuation) == COROUTINE_SUSPENDED){
            return COROUTINE_SUSPENDED
        }
    }
    if (continuation.label == 1) {
        counter = (counter as Int) + 1
        println("Counter: $counter")
        println("After")
        return Unit
    }
    error("Impossible")
}

class MyFunctionContinuation(
    val completion: Continuation<Unit>
) : Continuation<Unit> {
    override val context: CoroutineContext
        get() = completion.context

    var result: Result<Unit>? = null
    var label = 0
    var counter = 0

    override fun resumeWith(result: Result<Unit>) {
        this.result = result
        val res = try {
            val r = myFunction(this)
            if (r == COROUTINE_SUSPENDED) return
            Result.success(r as Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
        completion.resumeWith(res)
    }
}


private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true }
}

fun delay(timeMillis: Long, continuation: Continuation<Unit>): Any {
    executor.schedule({ continuation.resume(Unit) }, timeMillis, TimeUnit.MILLISECONDS)
    return COROUTINE_SUSPENDED
}

fun main() {
    val EMPTY_CONTINUATION = object : Continuation<Unit> {
        override val context: CoroutineContext = EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) {
            // This is root coroutine, we don't need anything in this example
        }
    }
    myFunction(EMPTY_CONTINUATION)
    Thread.sleep(2000)
    // Needed to prevent main() from finishing immediately.
}

private val COROUTINE_SUSPENDED = Any()
```


```
suspend fun printUser(token: String) {
  println("Before")
  val userId = getUserId(token) // suspending
  println("Got userId: $userId")
  val userName = getUserName(userId, token) // suspending
  println(User(userId, userName))
  println("After")
}
```


```
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.*

fun printUser(
    token: String,
    continuation: Continuation<*>
): Any {
    val continuation = continuation as? PrintUserContinuation
        ?: PrintUserContinuation(
            continuation as Continuation<Unit>,
            token
        )

    var result: Result<Any>? = continuation.result
    var userId: String? = continuation.userId
    val userName: String

    if (continuation.label == 0) {
        println("Before")
        continuation.label = 1
        val res = getUserId(token, continuation)
        if (res == COROUTINE_SUSPENDED) {
            return COROUTINE_SUSPENDED
        }
        result = Result.success(res)
    }
    if (continuation.label == 1) {
        userId = result!!.getOrThrow() as String
        println("Got userId: $userId")
        continuation.label = 2
        continuation.userId = userId
        val res = getUserName(userId, continuation)
        if (res == COROUTINE_SUSPENDED) {
            return COROUTINE_SUSPENDED
        }
        result = Result.success(res)
    }
    if (continuation.label == 2) {
        userName = result!!.getOrThrow() as String
        println(User(userId as String, userName))
        println("After")
        return Unit
    }
    error("Impossible")
}

class PrintUserContinuation(
    val completion: Continuation<Unit>,
    val token: String
) : Continuation<String> {
    override val context: CoroutineContext
        get() = completion.context

    var label = 0
    var result: Result<Any>? = null
    var userId: String? = null

    override fun resumeWith(result: Result<String>) {
        this.result = result
        val res = try {
            val r = printUser(token, this)
            if (r == COROUTINE_SUSPENDED) return
            Result.success(r as Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
        completion.resumeWith(res)
    }
}


fun main() {
    toStart()
}

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true }
}

data class User(val id: String, val name: String)
object ApiException : Throwable("Fake API exception")

fun getUserId(token: String, continuation: Continuation<String>): Any {
    executor.schedule({ continuation.resume("SomeId") }, 1000, TimeUnit.MILLISECONDS)
    return COROUTINE_SUSPENDED
}

fun getUserName(userId: String, continuation: Continuation<String>): Any {
    executor.schedule({
        continuation.resume("SomeName")
        //        continuation.resumeWithException(ApiException)
    }, 1000, TimeUnit.MILLISECONDS)
    return COROUTINE_SUSPENDED
}

fun toStart() {
    val EMPTY_CONTINUATION = object : Continuation<Unit> {
        override val context: CoroutineContext = EmptyCoroutineContext

        override fun resumeWith(result: kotlin.Result<Unit>) {
            if (result.isFailure) {
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
    printUser("SomeToken", EMPTY_CONTINUATION)
    Thread.sleep(3000)
    // Needed to prevent the function from finishing immediately.
}

private fun Result<*>.throwOnFailure() {
    if (isFailure) throw exceptionOrNull()!!
}

private val COROUTINE_SUSPENDED = Any()
```


```
suspend fun a() {
    val user = readUser()
    b()
    b()
    b()
    println(user)
}

suspend fun b() {
    for (i in 1..10) {
        c(i)
    }
}

suspend fun c(i: Int) {
    delay(i * 100L)
    println("Tick")
}
```


```
override fun resumeWith(result: Result<String>) {
    this.result = result
    val res = try {
        val r = printUser(token, this)
        if (r == COROUTINE_SUSPENDED) return
        Result.success(r as Unit)
    } catch (e: Throwable) {
        Result.failure(e)
    }
    completion.resumeWith(res)
}
```


```
internal abstract class BaseContinuationImpl(
    val completion: Continuation<Any?>?
) : Continuation<Any?>, CoroutineStackFrame, Serializable {
    // This implementation is final. This fact is used to
    // unroll resumeWith recursion.
    final override fun resumeWith(result: Result<Any?>) {
        // This loop unrolls recursion in
        // current.resumeWith(param) to make saner and
        // shorter stack traces on resume
        var current = this
        var param = result
        while (true) {
            // Invoke "resume" debug probe on every resumed
            // continuation, so that a debugging library
            // infrastructure can precisely track what part
            // of suspended call stack was already resumed
            probeCoroutineResumed(current)
            with(current) {
                val completion = completion!! // fail fast
                // when trying to resume continuation
                // without completion
                val outcome: Result<Any?> =
                    try {
                        val outcome = invokeSuspend(param)
                        if (outcome === COROUTINE_SUSPENDED)
                            return
                        Result.success(outcome)
                    } catch (exception: Throwable) {
                        Result.failure(exception)
                    }
                releaseIntercepted()
                // this state machine instance is terminating
                if (completion is BaseContinuationImpl) {
                    // unrolling recursion via loop
                    current = completion
                    param = outcome
                } else {
                    // top-level completion reached --
                    // invoke and return
                    completion.resumeWith(outcome)
                    return
                }
            }
        }
    }

    // ...
}
```