```
interface Flow<out T> {
    suspend fun collect(collector: FlowCollector<T>)
}
```


```
interface Iterable<out T> {
    operator fun iterator(): Iterator<T>
}

interface Sequence<out T> {
    operator fun iterator(): Iterator<T>
}
```


```
fun allUsers(): List<User> =
    api.getAllUsers().map { it.toUser() }
```


```
fun getList(): List<Int> = List(3) {
    Thread.sleep(1000)
    "User$it"
}

fun main() {
    val list = getList()
    println("Function started")
    list.forEach { println(it) }
}
// (3 sec)
// Function started
// User0
// User1
// User2
```


```
fun getSequence(): Sequence<String> = sequence {
    repeat(3) {
        Thread.sleep(1000)
        yield("User$it")
    }
}

fun main() {
    val list = getSequence()
    println("Function started")
    list.forEach { println(it) }
}
// Function started
// (1 sec)
// User0
// (1 sec)
// User1
// (1 sec)
// User2
```


```
fun getSequence(): Sequence<String> = sequence {
    repeat(3) {
        delay(1000) // Compilation error
        yield("User$it")
    }
}
```


```
// Don't do that, we should use Flow instead of Sequence
fun allUsersSequence(
    api: UserApi
): Sequence<User> = sequence {
        var page = 0
        do {
            val users = api.takePage(page++) // suspending,
            // so compilation error
            yieldAll(users)
        } while (!users.isNullOrEmpty())
    }
```


```
//1
import kotlinx.coroutines.*

fun getSequence(): Sequence<String> = sequence {
    repeat(3) {
        Thread.sleep(1000)
        // the same result as if there were delay(1000) here
        yield("User$it")
    }
}

suspend fun main() {
    withContext(newSingleThreadContext("main")) {
        launch {
            repeat(3) {
                delay(100)
                println("Processing on coroutine")
            }
        }

        val list = getSequence()
        list.forEach { println(it) }
    }
}
// (1 sec)
// User0
// (1 sec)
// User1
// (1 sec)
// User2
// Processing on coroutine
// (0.1 sec)
// Processing on coroutine
// (0.1 sec)
// Processing on coroutine
```


```
//2
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun getFlow(): Flow<String> = flow {
    repeat(3) {
        delay(1000)
        emit("User$it")
    }
}

suspend fun main() {
    withContext(newSingleThreadContext("main")) {
        launch {
            repeat(3) {
                delay(100)
                println("Processing on coroutine")
            }
        }

        val list = getFlow()
        list.collect { println(it) }
    }
}
// (0.1 sec)
// Processing on coroutine
// (0.1 sec)
// Processing on coroutine
// (0.1 sec)
// Processing on coroutine
// (1 - 3 * 0.1 = 0.7 sec)
// User0
// (1 sec)
// User1
// (1 sec)
// User2
```


```
fun allUsersFlow(
    api: UserApi
): Flow<User> = flow {
    var page = 0
    do {
        val users = api.takePage(page++) // suspending
        emitAll(users)
    } while (!users.isNullOrEmpty())
}
```


```
//3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// Notice, that this function is not suspending
// and does not need CoroutineScope
fun usersFlow(): Flow<String> = flow {
    repeat(3) {
        delay(1000)
        val ctx = currentCoroutineContext()
        val name = ctx[CoroutineName]?.name
        emit("User$it in $name")
    }
}

suspend fun main() {
    val users = usersFlow()

    withContext(CoroutineName("Name")) {
        val job = launch {
            // collect is suspending
            users.collect { println(it) }
        }

        launch {
            delay(2100)
            println("I got enough")
            job.cancel()
        }
    }
}
// (1 sec)
// User0 in Name
// (1 sec)
// User1 in Name
// (0.1 sec)
// I got enough
```


```
@Dao
interface MyDao {
    @Query("SELECT * FROM somedata_table")
    fun getData(): Flow<List<SomeData>>
}
```


```
suspend fun getOffers(
    sellers: List<Seller>
): List<Offer> = coroutineScope {
    sellers
        .map { seller ->
            async { api.requestOffers(seller.id) }
        }
        .flatMap { it.await() }
}
```


```
suspend fun getOffers(
    sellers: List<Seller>
): List<Offer> = sellers
    .asFlow()
    .flatMapMerge(concurrency = 20) { seller ->
        suspend { api.requestOffers(seller.id) }.asFlow()
    }
    .toList()
```