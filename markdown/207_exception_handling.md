```
//1
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//sampleStart
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
//sampleEnd
```


```
//2
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//sampleStart
fun main(): Unit = runBlocking {
    // Don't wrap in a try-catch here. It will be ignored.
    try {
        launch {
            delay(1000)
            throw Error("Some error")
        }
    } catch (e: Throwable) { // nope, does not help here
        println("Will not be printed")
    }

    launch {
        delay(2000)
        println("Will not be printed")
    }
}
// Exception in thread "main" java.lang.Error: Some error...
//sampleEnd
```


```
//3
import kotlinx.coroutines.*

//sampleStart
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
}
// Exception...
// Will be printed
//sampleEnd
```


```
//4
import kotlinx.coroutines.*

//sampleStart
fun main(): Unit = runBlocking {
    // Don't do that, SupervisorJob with one children
    // and no parent works similar to just Job
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
//sampleEnd
```


```
//5
import kotlinx.coroutines.*

//sampleStart
fun main(): Unit = runBlocking {
  val job = SupervisorJob()
  launch(job) {
      delay(1000)
      throw Error("Some error")
  }
  launch(job) {
      delay(2000)
      println("Will be printed")
  }
  job.join()
}
// (1 sec)
// Exception...
// (1 sec)
// Will be printed
//sampleEnd
```


```
//6
import kotlinx.coroutines.*

//sampleStart
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
  }
  delay(1000)
  println("Done")
}
// Exception...
// Will be printed
// (1 sec)
// Done
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
// DON'T DO THAT!
suspend fun sendNotifications(
   notifications: List<Notification>
) = withContext(SupervisorJob()) {
   for (notification in notifications) {
       launch {
           client.send(notification)
       }
   }
}
```


```
//7
import kotlinx.coroutines.*

//sampleStart
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
//sampleEnd
```


```
//8
import kotlinx.coroutines.*

object MyNonPropagatingException : CancellationException()

suspend fun main(): Unit = coroutineScope {
  launch { // 1
      launch { // 2
          delay(2000)
          println("Will not be printed")
      }
      throw MyNonPropagatingException // 3
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
//9
import kotlinx.coroutines.*

//sampleStart
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
//sampleEnd
```