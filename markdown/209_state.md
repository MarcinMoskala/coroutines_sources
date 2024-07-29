```
//1
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
    var num = 0
    coroutineScope {
        repeat(10_000) {
            launch { // uses Dispatchers.Default
                delay(10)
                num++
            }
        }
    }
    print(num)
}
// The result very unlikely to be 10000, should be much smaller
```


```
//2
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class User(val name: String)

suspend fun main() {
    var users = listOf<User>()
    coroutineScope {
        repeat(10_000) { i ->
            launch {
                delay(10)
                users += User("User$i")
            }
        }
    }
    print(users.size)
}
// The result very unlikely to be 10000, likely around 3000
```


```
//3
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class User(val name: String)

suspend fun main() {
    val users = mutableListOf<User>()
    coroutineScope {
        for (i in 1..10000) {
            launch {
                delay(10)
                users += User("User$i")
            }
        }
    }
    println(users.size)
}
// number around 9500
// or
// ArrayIndexOutOfBoundsException
```


```
//4
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

suspend fun main() {
    var num = AtomicInteger(0)
    coroutineScope {
        repeat(10_000) {
            launch {
                delay(10)
                num.incrementAndGet()
            }
        }
    }
    print(num) // 10000
}
```


```
//5
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

suspend fun main() {
    var str = AtomicReference("")
    coroutineScope {
        repeat(10_000) {
            launch {
                delay(10)
                str.updateAndGet { it + "A" }
            }
        }
    }
    print(str.get().length) // 10000
}
```


```
//6
import kotlin.concurrent.thread

@Volatile
var number: Int = 0

@Volatile
var ready: Boolean = false

fun main() {
    thread {
        while (!ready) {
            Thread.yield()
        }
        println(number)
    }
    number = 42
    ready = true
}
```


```
//7
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Volatile
var num = 0

suspend fun main() {
    coroutineScope {
        repeat(10_000) {
            launch {
                delay(10)
                num++
            }
        }
    }
    print(num) // around 9800, not 10000
}
```


```
//8
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

data class User(val name: String)

suspend fun main() {
    val users = ConcurrentHashMap.newKeySet<User>()
    coroutineScope {
        for (i in 1..10000) {
            launch {
                delay(10)
                users += User("User$i")
            }
        }
    }
    println(users.size) // 10000
}
```


```
class ProductRepository(
    val client: ProductClient,
) {
    private val cache = ConcurrentHashMap<Int, Product>()

    suspend fun getProduct(id: Int): Product? {
        val product = cache[id]
        if (product != null) {
            return product
        } else {
            val fetchedProduct = client.fetchProduct(id)
            if (fetchedProduct != null) {
                cache[id] = fetchedProduct
            }
            return fetchedProduct
        }
    }

    // ...
}
```


```
//9
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

var counter = ConcurrentHashMap<String, Int>()

// Incorrect implementation
fun increment(key: String) {
    val value = counter[key] ?: 0
    counter[key] = value + 1
}

suspend fun main() {
    coroutineScope {
        repeat(10_000) {
            launch { // uses Dispatchers.Default
                increment("A")
            }
        }
    }
    print(counter) // {A=7162}
}
```


```
//10
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

var counter = ConcurrentHashMap<String, Int>()

// Correct implementation
fun increment(key: String) {
    counter.compute(key) { _, v -> (v ?: 0) + 1 }
}

suspend fun main() {
    coroutineScope {
        repeat(10_000) {
            launch { // uses Dispatchers.Default
                increment("A")
            }
        }
    }
    print(counter) // {A=10000}
}
```


```
class EventSender {
    private val waiting = ConcurrentHashMap.newKeySet<Event>()

    fun schedule(event: Event) {
        waiting.add(event)
    }

    fun sendBundle() {
        // Incorrect implementation!
        // Some elements might be removed without being sent
        waiting.forEach { sent(it) }
        waiting.clear()
    }
}
```


```
class EventSender {
    private val waiting = mutableSetOf<Event>()
    private val lock = Any()
    
    fun schedule(event: Event) = synchronized(lock) {
        waiting.add(event)
    }

    fun sendBundle() = synchronized(lock) {
        waiting.forEach { sent(it) }
        waiting.clear()
    }
}
```


```
//11
import kotlinx.coroutines.*

suspend fun main() {
    var num = 0
    val lock = Any()
    coroutineScope {
        repeat(10_000) {
            launch {
                delay(10)
                synchronized(lock) {
                    num++
                }
            }
        }
    }
    print(num) // 10000
}
```


```
synchronized(this) { ... }
// or
val lock = Any()
synchronized(lock) { ... }
```


```
class ProductRepository(
    val client: ProductClient,
) {
    private val cache = mutableMapOf<Int, Product>()
    private val lock = Any()

    suspend fun getProduct(id: Int): Product? = 
        synchronized(lock) {
            val product = cache[id]
            if (product != null) {
                return product
            } else {
                val fetchedProduct = client.fetchProduct(id)
                if (fetchedProduct != null) {
                    cache[id] = fetchedProduct
                }
                return fetchedProduct
            }
        }

    // ...
}
```


```
class ProductRepository(
    val client: ProductClient,
) {
    private val cache = mutableMapOf<Int, Product>()
    private val lock = Any()

    suspend fun getProduct(id: Int): Product? {
        val product = synchronized(lock) { cache[id] }
        if (product != null) {
            return product
        } else {
            val fetchedProduct = client.fetchProduct(id)
            if (fetchedProduct != null) {
                synchronized(lock) {
                    cache[id] = fetchedProduct
                }
            }
            return fetchedProduct
        }
    }

    // ...
}
```


```
//12
import kotlin.concurrent.thread

val lock1 = Any()
val lock2 = Any()

fun f1() = synchronized(lock1) {
    Thread.sleep(1000L)
    synchronized(lock2) {
        println("f1")
    }
}

fun f2() = synchronized(lock2) {
    Thread.sleep(1000L)
    synchronized(lock1) {
        println("f2")
    }
}

fun main() {
    thread { f1() }
    thread { f2() }
}
```


```
//13
import kotlinx.coroutines.*

suspend fun main() {
    var num = 0
    val dispatcher = Dispatchers.IO.limitedParallelism(1)
    coroutineScope {
        repeat(10_000) {
            launch(dispatcher) {
                delay(10)
                num++
            }
        }
    }
    print(num) // 10000
}
```


```
class ProductRepository(
    val client: ProductClient,
) {
    private val cache = mutableMapOf<Int, Product>()
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)

    suspend fun getProduct(id: Int): Product? = 
        withContext(dispatcher) {
            val product = cache[id]
            if (product != null) {
                product
            } else {
                val fetchedProduct = client.fetchProduct(id)
                if (fetchedProduct != null) {
                    cache[id] = fetchedProduct
                }
                fetchedProduct
            }
        }

    // ...
}
```


```
//14
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MessagesRepository {
    private val messages = mutableListOf<String>()
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)
    suspend fun add(message: String) = withContext(dispatcher) {
        delay(1000) // we simulate network call
        messages.add(message)
    }
}

suspend fun main() {
    val repo = MessagesRepository()
    val timeMillis = measureTimeMillis {
        coroutineScope {
            repeat(5) {
                launch {
                    repo.add("Message$it")
                }
            }
        }
    }
    println(timeMillis) // 1058
}
```


```
class ProductRepository(
    val client: ProductClient,
    dispatcher: CoroutineDispatcher
) {
    private val cache = mutableMapOf<Int, Product>()
    private val dispatcher = dispatcher.limitedParallelism(1)

    suspend fun getProduct(id: Int): Product? =
        withContext(dispatcher) {
            val product = cache[id]
            if (product != null) {
                product
            } else {
                val fetchedProduct = client.fetchProduct(id)
                if (fetchedProduct != null) {
                    cache[id] = fetchedProduct
                }
                fetchedProduct
            }
        }

    // ...
}
```


```
//15
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

suspend fun main() = coroutineScope {
    repeat(5) {
        launch {
            delayAndPrint()
        }
    }
}

val mutex = Mutex()

suspend fun delayAndPrint() {
    mutex.lock()
    delay(1000)
    println("Done")
    mutex.unlock()
}
// (1 sec)
// Done
// (1 sec)
// Done
// (1 sec)
// Done
// (1 sec)
// Done
// (1 sec)
// Done
```


```
//16
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

suspend fun main() {
    val mutex = Mutex()
    var num = 0
    coroutineScope {
        repeat(10_000) {
            launch {
                mutex.withLock {
                    num++
                }
            }
        }
    }
    print(num) // 10000
}
```


```
//17
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

suspend fun main() {
    val mutex = Mutex()
    println("Started")
    mutex.withLock {
        mutex.withLock {
            println("Will never be printed")
        }
    }
}
// Started
// (runs forever)
```


```
//18
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.measureTimeMillis

class MessagesRepository {
    private val messages = mutableListOf<String>()
    private val mutex = Mutex()

    suspend fun add(message: String) = mutex.withLock {
        delay(1000) // we simulate network call
        messages.add(message)
    }
}

suspend fun main() {
    val repo = MessagesRepository()
    val timeMillis = measureTimeMillis {
        coroutineScope {
            repeat(5) {
                launch {
                    repo.add("Message$it")
                }
            }
        }
    }
    println(timeMillis) // ~5120
}
```


```
class MongoUserRepository(
    //...
) : UserRepository {
    private val mutex = Mutex()

    override suspend fun updateUser(
        userId: String,
        userUpdate: UserUpdate
    ): Unit = mutex.withLock {
        val currentUser = getUser(userId) // Deadlock!
        deleteUser(userId) // Deadlock!
        addUser(currentUser.updated(userUpdate)) // Deadlock!
    }

    override suspend fun getUser(
        userId: String
    ): User = mutex.withLock {
        // ...
    }

    override suspend fun deleteUser(
        userId: String
    ): Unit = mutex.withLock {
        // ...
    }

    override suspend fun addUser(
        user: User
    ): User = mutex.withLock {
        // ...
    }
}
```


```
//19
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

suspend fun main() {
    val mutex = Mutex()
    println("Started")
    mutex.withLock("main()") {
        mutex.withLock("main()") {
            println("Will never be printed")
        }
    }
}
// Started
// IllegalStateException: This mutex is already
// locked by the specified owner: main()
```


```
//20
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

suspend fun main() = coroutineScope {
    val semaphore = Semaphore(2)

    repeat(5) {
        launch {
            semaphore.withPermit {
                delay(1000)
                print(it)
            }
        }
    }
}
// 01
// (1 sec)
// 23
// (1 sec)
// 4
```


```
class LimitedNetworkUserRepository(
    private val api: UserApi
) {
    // We limit to 10 concurrent requests
    private val semaphore = Semaphore(10)

    suspend fun requestUser(userId: String) =
        semaphore.withPermit {
            api.requestUser(userId)
        }
}
```