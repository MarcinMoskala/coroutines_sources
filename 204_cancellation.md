```
//1
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope

//sampleStart
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
// Printing 0
// Printing 1
// Printing 2
// Printing 3
// Printing 4
// Cancelled successfully
//sampleEnd
```


```
//2
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//sampleStart
suspend fun main() = coroutineScope {
  val job = launch {
      repeat(1_000) { i ->
          delay(100)
          Thread.sleep(100) // We simulate long operation
          println("Printing $i")
      }
  }

  delay(1000)
  job.cancel()
  println("Cancelled successfully")
}
// Printing 0
// Printing 1
// Printing 2
// Printing 3
// Cancelled successfully
// Printing 4
//sampleEnd
```


```
//3
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//sampleStart
suspend fun main() = coroutineScope {
  val job = launch {
      repeat(1_000) { i ->
          delay(100)
          Thread.sleep(100) // We simulate long operation
          println("Printing $i")
      }
  }

  delay(1000)
  job.cancel()
  job.join()
  println("Cancelled successfully")
}
// Printing 0
// Printing 1
// Printing 2
// Printing 3
// Printing 4
// Cancelled successfully
//sampleEnd
```


```
// The most explicit function name I've ever seen
public suspend fun Job.cancelAndJoin() {
   cancel()
   return join()
}
```


```
//4
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
   val job = Job()
   launch(job) {
       repeat(1_000) { i ->
           delay(200)
           println("Printing $i")
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
//sampleEnd
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
//5
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
   val job = Job()
   launch(job) {
       try {
           repeat(1_000) { i ->
               delay(200)
               println("Printing $i")
           }
       } catch (e: CancellationException) {
           println(e)
           throw e
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
// JobCancellationException...
// Cancelled successfully
//sampleEnd
```


```
//6
import kotlinx.coroutines.*
import kotlin.random.Random

//sampleStart
suspend fun main(): Unit = coroutineScope {
   val job = Job()
   launch(job) {
       try {
           delay(Random.nextLong(2000))
           println("Done")
       } finally {
           print("Will always be printed")
       }
   }
   delay(1000)
   job.cancelAndJoin()
}
// Will always be printed
// (or)
// Done
// Will always be printed
//sampleEnd
```


```
//7
import kotlinx.coroutines.*
import kotlin.random.Random

suspend fun main(): Unit = coroutineScope {
   val job = Job()
   launch(job) {
       try {
           delay(2000)
           println("Job is done")
       } finally {
           println("Finally")
           launch { // will be ignored
               println("Will not be printed")
           }
           delay(1000) // here exception is thrown
           println("Will not be printed")
       }
   }
   delay(1000)
   job.cancelAndJoin()
   println("Cancel done")
}
// (1 sec)
// Finally
// Cancel done
```


```
//8
import kotlinx.coroutines.*
import kotlin.random.Random

//sampleStart
suspend fun main(): Unit = coroutineScope {
   val job = Job()
   launch(job) {
       try {
           delay(200)
           println("Coroutine finished")
       } finally {
           println("Finally")
           withContext(NonCancellable) {
               delay(1000L)
               println("Cleanup done")
           }
       }
   }
   delay(100)
   job.cancelAndJoin()
   println("Done")
}
// Finally
// Cleanup done
// Done
//sampleEnd
```


```
//9
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
   val job = launch {
       delay(1000)
   }
   job.invokeOnCompletion { exception: Throwable? ->
       println("Finished")
   }
   delay(400)
   job.cancelAndJoin()
}
// Finished
//sampleEnd
```


```
//10
import kotlinx.coroutines.*
import kotlin.random.Random

//sampleStart
suspend fun main(): Unit = coroutineScope {
   val job = launch {
       delay(Random.nextLong(2400))
       println("Finished")
   }
   delay(800)
   job.invokeOnCompletion { exception: Throwable? ->
       println("Will always be printed")
       println("The exception was: $exception")
   }
   delay(800)
   job.cancelAndJoin()
}
// Will always be printed
// The exception was:
// kotlinx.coroutines.JobCancellationException
// (or)
// Finished
// Will always be printed
// The exception was null
//sampleEnd
```


```
//11
import kotlinx.coroutines.*

//sampleStart
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
//sampleEnd
```


```
//12
import kotlinx.coroutines.*

//sampleStart
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
//sampleEnd
```


```
public val CoroutineScope.isActive: Boolean
   get() = coroutineContext[Job]?.isActive ?: true
```


```
//13
import kotlinx.coroutines.*

//sampleStart
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
//sampleEnd
```


```
//14
import kotlinx.coroutines.*

//sampleStart
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
//sampleEnd
```


```
suspend fun someTask() = suspendCancellableCoroutine { cont ->
   cont.invokeOnCancellation {
       // do cleanup
   }
   // rest of the implementation
}
```


```
suspend fun getOrganizationRepos(
   organization: String
): List<Repo> =
   suspendCancellableCoroutine { continuation ->
       val orgReposCall = apiService
           .getOrganizationRepos(organization)
       orgReposCall.enqueue(object : Callback<List<Repo>> {
           override fun onResponse(
               call: Call<List<Repo>>,
               response: Response<List<Repo>>
           ) {
               if (response.isSuccessful) {
                   val body = response.body()
                   if (body != null) {
                       continuation.resume(body)
                   } else {
                       continuation.resumeWithException(
                           ResponseWithEmptyBody
                       )
                   }
               } else {
                   continuation.resumeWithException(
                       ApiException(
                           response.code(),
                           response.message()
                       )
                   )
               }
           }

           override fun onFailure(
               call: Call<List<Repo>>,
               t: Throwable
           ) {
               continuation.resumeWithException(t)
           }
       })
       continuation.invokeOnCancellation {
           orgReposCall.cancel()
       }
   }
```


```
class GithubApi {
   @GET("orgs/{organization}/repos?per_page=100")
   suspend fun getOrganizationRepos(
       @Path("organization") organization: String
   ): List<Repo>
}

```