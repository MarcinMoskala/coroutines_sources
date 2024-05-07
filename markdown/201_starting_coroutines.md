```
//1
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    Thread.sleep(2000L)
}
// Hello,
// (1 sec)
// World!
// World!
// World!
```


```
//2
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
    val job1 = GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    val job2 = GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    val job3 = GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    job1.join()
    job2.join()
    job3.join()
}
// Hello,
// (1 sec)
// World!
// World!
// World!
```


```
//3
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

suspend fun main() {
    val value1 = GlobalScope.async {
        delay(2000L)
        1
    }
    val value2 = GlobalScope.async {
        delay(2000L)
        2
    }
    val value3 = GlobalScope.async {
        delay(2000L)
        3
    }
    println("Calculating")
    print(value1.await())
    print(value2.await())
    print(value3.await())
}
// Calculating
// (2 sec)
// 123 (order is guaranteed, as we await for values in order)
```


```
//4
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

suspend fun main() {
    val value = GlobalScope.async {
        delay(2000L)
        1
    }
    println("Calculating")
    print(value.await())
    print(value.await())
    print(value.await())
}
// Calculating
// (2 sec)
// 111
```


```
scope.launch {
    val news = async { newsRepo.getNews() }
    val newsSummary = async { newsRepo.getNewsSummary() }
    view.showNews(newsSummary.await(), news.await())
}
```


```
//5
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        delay(1000L)
        println("World!")
    }
    runBlocking {
        delay(1000L)
        println("World!")
    }
    runBlocking {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}
// (1 sec)
// World!
// (1 sec)
// World!
// (1 sec)
// World!
// Hello,
```


```
//6
import kotlinx.coroutines.runBlocking
annotation class Test

fun main() = runBlocking {
    // ...
}

class MyTests {

    @Test
    fun `a test`() = runBlocking {

    }
}
```


```
fun runDataMigrationScript() = runBlocking {
    val sourceData = readDataFromSource()
    val transformedData = transformData(sourceData)
    writeDataToTarget(transformedData)
}
```


```
fun <T> runBlocking(
    context: CoroutineContext, 
    block: suspend CoroutineScope.() -> T
): T 

fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job

fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T>
```


```
//7
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch { // same as this.launch
        delay(1000L)
        println("World!")
    }
    launch { // same as this.launch
        delay(1000L)
        println("World!")
    }
    launch { // same as this.launch
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}
// Hello,
// (1 sec)
// World!
// World!
// World!
```


```
//8
import kotlinx.coroutines.*

fun main() = runBlocking {
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}
// Hello,
```


```
object GlobalScope : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}
```


```
//9
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope

suspend fun main() {
    coroutineScope {
        delay(1000L)
        println("World!")
    }
    coroutineScope {
        delay(1000L)
        println("World!")
    }
    coroutineScope {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}
// (1 sec)
// World!
// (1 sec)
// World!
// (1 sec)
// World!
// Hello,
```


```
//10
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

suspend fun main() {
    println("A")
    val a: Int = coroutineScope {
        delay(1000L)
        10
    }
    println("B")
    val b: Int = coroutineScope {
        delay(1000L)
        20
    }
    println("C")
    println(a + b)
}
// A
// (1 sec)
// B
// (1 sec)
// C
// 30
```


```
//11
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

suspend fun main() = coroutineScope {
    println("A")
    val a: Deferred<Int> = async {
        delay(1000L)
        10
    }
    println("B")
    val b: Deferred<Int> = async {
        delay(1000L)
        20
    }
    println("C")
    println(a.await() + b.await())
}
// A
// B
// C
// (2 sec)
// 30
```


```
suspend fun getUserProfile(
    userId: String
): UserProfile = coroutineScope {
    val user = async { getPublicUserDetails(userId) }
    val articles = async { getUserArticles(userId) }
    UserProfile(user.await(), articles.await())
}
```


```
suspend fun getPublicUserDetails(
    userId: String
): List<ArticleDetails> = coroutineScope {
    articleRepo.getArticles(userId)
        .filter { it.isPublic }
        .map { async { getArticleDetails(it.id) } }
        .awaitAll()
}
```


```
//12
import kotlinx.coroutines.*

suspend fun longTask() = coroutineScope {
    launch {
        delay(1000)
        println("Finished task 1")
    }
    launch {
        delay(2000)
        println("Finished task 2")
    }
}

suspend fun main() {
    println("Before")
    longTask()
    println("After")
}
// Before
// (1 sec)
// Finished task 1
// (1 sec)
// Finished task 2
// After
```


```
suspend fun updateUser() = coroutineScope {
    // ...
    
    // Don't
    launch { sendEvent(UserSunchronized) }
    // should be (to call synchronously)
    // sendEvent(UserSunchronized)
    // or (to call asynchronously), if we have scope property
    // scope.launch { sendEvent(UserSunchronized) }
}
```