```
suspend fun fetchUserDetails(): UserDetails = coroutineScope {
    val userData = async { fetchUserData() }
    val userPreferences = async { fetchUserPreferences() }
    UserDetails(userData.await(), userPreferences.await())
}
```


```
//1
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    launch {
        launch {
            delay(1000)
            throw Error("Some error")
        }
        launch {
            delay(2000)
            println("Will not be printed")
        }
        launch {
            delay(500) // faster than the exception
            println("Will be printed")
        }
    }
    launch {
        delay(2000)
        println("Will not be printed")
    }
}
// Will be printed
// Exception in thread "main" java.lang.Error: Some error...
```


```
// Exception in fetchUserPreferences is ignored
suspend fun fetchUserDetails(): UserDetails =
    coroutineScope {
        val userData = async { fetchUserData() }
        val userPreferences = async {
            try {
                fetchUserPreferences()
            } catch (e: Throwable) {
                println("Error in fetchUserPreferences: $e")
                null
            }
        }

        UserDetails(userData.await(), userPreferences.await())
    }
```


```
// Exception in fetchUserPreferences cancells fetchUserDetails,
// and makes fetchUserData return null
suspend fun fetchUserDetails(): UserDetails? = try {
        coroutineScope {
            val userData = async { fetchUserData() }
            val userPreferences = async { fetchUserPreferences() }

            UserDetails(userData.await(), userPreferences.await())
        }
    } catch (e: Throwable) {
        println("Error in fetchUserDetails: $e")
        null
    }
```


```
//2
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val scope = CoroutineScope(SupervisorJob())
    scope.launch {
        delay(1000)
        throw Error("Some error")
    }
    scope.launch {
        delay(2000)
        println("Will be printed")
    }
    delay(3000)
    println(scope.isActive)
}
// (1 sec)
// Exception...
// (2 sec)
// Will be printed
// true
```


```
//3
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    // DON'T DO THAT!
    launch(SupervisorJob()) { // 1
        launch {
            delay(1000)
            throw Error("Some error")
        }

        launch {
            delay(2000)
            println("Will not be printed")
        }
    }

    delay(3000)
}
// Exception...
```


```
//4
import kotlinx.coroutines.*

// DON'T DO THAT!
fun main(): Unit = runBlocking(SupervisorJob()) {
    launch { // 1
        delay(1000)
        throw Error("Some error")
    }
    launch { // 2
        delay(2000)
        println("Will not be printed")
    }
}
// Exception...
```


```
//5
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    supervisorScope {
        launch {
            delay(1000)
            throw Error("Some error")
        }
        launch {
            delay(2000)
            println("Will be printed")
        }
        launch {
            delay(2000)
            println("Will be printed")
        }
    }
    println("Done")
}
// (1 sec)
// Exception...
// Will be printed
// Will be printed
// Done
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
suspend fun notifyAnalytics(actions: List<UserAction>) =
    withContext(dispatcher) {
        supervisorScope {
            actions.forEach { action ->
                launch {
                    notifyAnalytics(action)
                }
            }
        }
    }
```


```
//6
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    // DON'T DO THAT!
    withContext(SupervisorJob()) {
        launch {
            delay(1000)
            throw Error("Some error")
        }
        launch {
            delay(2000)
            println("Will be printed")
        }
        launch {
            delay(2000)
            println("Will be printed")
        }
    }
    delay(1000)
    println("Done")
}
// (1 sec)
// Exception...
```


```
//7
import kotlinx.coroutines.*

class MyException : Throwable()

suspend fun main() = supervisorScope {
    val str1 = async<String> {
        delay(1000)
        throw MyException()
    }

    val str2 = async {
        delay(2000)
        "Text2"
    }

    try {
        println(str1.await())
    } catch (e: MyException) {
        println(e)
    }

    println(str2.await())
}
// MyException
// Text2
```


```
//8
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val handler =
        CoroutineExceptionHandler { ctx, exception ->
            println("Caught $exception")
        }
    val scope = CoroutineScope(SupervisorJob() + handler)
    scope.launch {
        delay(1000)
        throw Error("Some error")
    }

    scope.launch {
        delay(2000)
        println("Will be printed")
    }

    delay(3000)
}
// Caught java.lang.Error: Some error
// Will be printed
```


```
val handler = CoroutineExceptionHandler { _, exception ->
    Log.e("CoroutineExceptionHandler", "Caught $exception")
}
```