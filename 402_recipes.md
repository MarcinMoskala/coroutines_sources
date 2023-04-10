```
suspend fun <T, R> List<T>.mapAsync(
   transformation: suspend (T) -> R
): List<R> = coroutineScope {
   this@mapAsync.map { async { transformation(it) } }
       .awaitAll()
}

// Example use
suspend fun getBestStudent(
   semester: String,
   repo: StudentsRepository
): Student =
   repo.getStudentIds(semester)
       .mapAsync { repo.getStudent(it) }
       .maxBy { it.result }
```


```
suspend fun <T, R> List<T>.mapAsync(
   concurrencyLimit: Int = Int.MAX_VALUE,
   transformation: suspend (T) -> R
): List<R> = coroutineScope {
   val semaphore = Semaphore(concurrencyLimit)
   this@mapAsync.map {
       async {
           semaphore.withPermit {
               transformation(it)
           }
       }
   }.awaitAll()
}
```


```
// Works because map is an inline function,
// so we can call suspend await in its lambda,
// even though this lambda itself is not suspending.
suspend fun getOffers(
    categories: List<Category>
): List<Offer> = coroutineScope {
    categories
        .map { async { api.requestOffers(it) } }
        .map { it.await() } // Prefer awaitAll
        .flatten()
}
```


```
suspend fun makeConnection(): Connection = TODO()

val connection by lazy { makeConnection() } // COMPILER ERROR
```


```
fun <T> suspendLazy(
   initializer: suspend () -> T
): suspend () -> T {
   TODO()
}
```


```
private val NOT_SET = Any()

fun <T> suspendLazy(
    initializer: suspend () -> T
): suspend () -> T {
    val mutex = Mutex()
    var holder: Any? = NOT_SET
    
    return {
        if (holder !== NOT_SET) holder as T
        else mutex.withLock {
            if (holder === NOT_SET) holder = initializer()
            holder as T
        }
    }
}
```


```
//1
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

fun <T> suspendLazy(
    initializer: suspend () -> T
): suspend () -> T {
    var initializer: (suspend () -> T)? = initializer
    val mutex = Mutex()
    var holder: Any? = Any()
    
    return {
        if (initializer == null) holder as T
        else mutex.withLock {
            initializer?.let {
                holder = it()
                initializer = null
            }
            holder as T
        }
    }
}

// Example use
suspend fun makeConnection(): String {
    println("Creating connection")
    delay(1000)
    return "Connection"
}

val getConnection = suspendLazy { makeConnection() }

suspend fun main() {
    println(getConnection())
    println(getConnection())
    println(getConnection())
}
// Creating connection
// (1 sec)
// Connection
// Connection
// Connection
```


```
class SuspendingCache<P : Any, T>(
    private val delegate: Cache<P, Deferred<T>>,
) : Cache<P, Deferred<T>> by delegate {
    
    suspend fun get(
        key: P, 
        build: suspend (key: P) -> T
    ): T = supervisorScope {
        fun getAsync() = 
            delegate.get(key) { async { build(it) } }!!
        
        var async: Deferred<T> = getAsync()
        
        // Do not consider failed request a valid cache entry
        if (async.isCancelled) {
            invalidate(key)
            async = getAsync()
        }
        
        try {
            async.await()
        } catch (e: CancellationException) {
            ensureActive()
            // We were waiting for different caller result,
            // but it was cancelled, so we start again
            get(key, build)
        }
    }
    
    // ...
}

fun <K : Any, V> Caffeine<in K, in Deferred<V>>
        .buildSuspending(): SuspendingCache<K, V> {
    val delegate = build<K, Deferred<V>>()
    return SuspendingCache(delegate)
}

// Example use
private val userCache = Caffeine.newBuilder()
   .expireAfterWrite(1, TimeUnit.MINUTES)
   .buildSuspending<String, User>()

suspend fun getUser(userId: String) = userCache
   .get(userId) { api.fetchUser(it) }
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


```
class ConnectionPool<K, V>(
    private val scope: CoroutineScope,
    private val builder: (K) -> Flow<V>,
) {
    private val connections = mutableMapOf<K, Flow<V>>()
    
    fun getConnection(key: K): Flow<V> = synchronized(this) {
        connections.getOrPut(key) {
            builder(key).shareIn(
                scope,
                started = SharingStarted.WhileSubscribed(),
            )
        }
    }
}

// Example use
private val scope = CoroutineScope(SupervisorJob())
private val messageConnections =
    ConnectionPool(scope) { threadId: String ->
        api.observeMessageThread(threadId)
    }

fun observeMessageThread(threadId: String) =
    messageConnections.getConnection(threadId)
```


```
class ConnectionPool<K, V>(
    private val scope: CoroutineScope,
    private val replay: Int = 0,
    private val stopTimeout: Duration,
    private val replayExpiration: Duration,
    private val builder: (K) -> Flow<V>,
) {
    private val connections = mutableMapOf<K, Flow<V>>()
    
    fun getConnection(key: K): Flow<V> = synchronized(this) {
        connections.getOrPut(key) {
            builder(key).shareIn(
                scope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis =
                    stopTimeout.inWholeMilliseconds,
                    replayExpirationMillis =
                    replayExpiration.inWholeMilliseconds,
                ),
                replay = replay,
            )
        }
    }
}
```


```
suspend fun <T> raceOf(
    racer: suspend CoroutineScope.() -> T,
    vararg racers: suspend CoroutineScope.() -> T
): T = coroutineScope {
    select {
        (listOf(racer) + racers).forEach { racer ->
            async { racer() }.onAwait {
                coroutineContext.job.cancelChildren()
                it
            }
        }
    }
}

// Example use
suspend fun a(): String {
    delay(1000)
    return "A"
}

suspend fun b(): String {
    delay(2000)
    return "B"
}

suspend fun c(): String {
    delay(3000)
    return "C"
}

suspend fun main(): Unit = coroutineScope {
    println(raceOf({ c() }))
    // (3 sec)
    // C
    println(raceOf({ b() }, { a() }))
    // (1 sec)
    // A
    println(raceOf({ b() }, { c() }))
    // (2 sec)
    // B
    println(raceOf({ b() }, { a() }, { c() }))
    // (1 sec)
    // A
}
```


```
fun makeConnection(config: ConnectionConfig) = api
    .startConnection(config)
    .retryWhen { e, attempt ->
        val times = 2.0.pow(attempt.toDouble()).toInt()
        delay(maxOf(10_000L, 100L * times))
        log.error(e) { "Error for $config" }
        e is ApiException && e.code !in 400..499
    }
```


```
inline fun <T> retry(operation: () -> T): T {
    while (true) {
        try {
            return operation()
        } catch (e: Throwable) {
            // no-op
        }
    }
}

// Usage
suspend fun requestData(): String {
    if (Random.nextInt(0, 10) == 0) {
        return "ABC"
    } else {
        error("Error")
    }
}

suspend fun main(): Unit = coroutineScope {
    println(retry { requestData() })
}
// (1 sec)
// ABC
```


```
inline fun <T> retryWhen(
    predicate: (Throwable, retries: Int) -> Boolean,
    operation: () -> T
): T {
    var retries = 0
    var fromDownstream: Throwable? = null
    while (true) {
        try {
            return operation()
        } catch (e: Throwable) {
            if (fromDownstream != null) {
                e.addSuppressed(fromDownstream)
            }
            fromDownstream = e
            if (e is CancellationException ||
                !predicate(e, retries++)
            ) {
                throw e
            }
        }
    }
}

// Usage
suspend fun requestWithRetry() = retryWhen(
    predicate = { e, retries ->
        val times = 2.0.pow(attempt.toDouble()).toInt()
        delay(maxOf(10_000L, 100L * times))
        log.error(e) { "Retried" }
        retries < 10 && e is IllegalStateException
    }
) {
    requestData()
}
```


```
inline suspend fun <T> retry(
    operation: () -> T
): T {
    var retries = 0
    while (true) {
        try {
            return operation()
        } catch (e: Exception) {
            val times = 2.0.pow(attempt.toDouble()).toInt()
            delay(maxOf(10_000L, 100L * times))
            if (e is CancellationException || retries >= 10){
                throw e
            }
            retries++
            log.error(e) { "Retrying" }
        }
    }
}

// Usage
suspend fun requestWithRetry() = retry {
    requestData()
}
```