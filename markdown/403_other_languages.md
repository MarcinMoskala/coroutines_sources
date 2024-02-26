```
class AnkiConnector(
    // ...
) {
    suspend fun checkConnection(): Boolean = ...
    
    suspend fun getDeckNames(): List<String> = ...
    
    suspend fun pushDeck(
        deckName: String,
        markdown: String
    ): AnkiConnectorResult = ...
    
    suspend fun pullDeck(
        deckName: String,
        currentMarkdown: String
    ): AnkiConnectorResult = ...
}
```


```
class AnkiConnectorBlocking {
    private val connector = AnkiConnector(/*...*/)
    
    fun checkConnection(): Boolean = runBlocking {
        connector.checkConnection()
    }
    
    fun getDeckNames(): List<String> = runBlocking {
        connector.getDeckNames()
    }
    
    fun pushDeck(
        deckName: String,
        markdown: String
    ): AnkiConnectorResult = runBlocking {
        connector.pushDeck(deckName, markdown)
    }
    
    fun pullDeck(
        deckName: String,
        currentMarkdown: String
    ): AnkiConnectorResult = runBlocking {
        connector.pullDeck(deckName, currentMarkdown)
    }
}
```


```
class AnkiConnector(
   // ...
) {
   suspend fun checkConnection(): Boolean = ...
    
   fun checkConnectionBlocking(): Boolean = runBlocking {
       connector.checkConnection()
   }
  
   // ...
}
```


```
class AnkiConnector(
   // ...
) {
   @JvmBlockingBridge
   suspend fun checkConnection(): Boolean = ...
}
```


```
// Java
class JavaTest {
    public static void main(String[] args) {
        AnkiConnector connector = new AnkiConnector();
        boolean connection = connector.checkConnection();
        // ...
    }
}
```


```
class AnkiConnectorCallback {
   private val connector = AnkiConnector(/*...*/)
   private val scope = CoroutineScope(SupervisorJob())
    
   fun checkConnection(
       callback: (Result<Boolean>) -> Unit
   ): Cancellable = toCallback(callback) {
       connector.checkConnection()
   }
    
   fun getDeckNames(
       callback: (Result<List<String>>) -> Unit
   ): Cancellable = toCallback(callback) {
       connector.getDeckNames()
   }
    
   fun pushDeck(
       deckName: String,
       markdown: String,
       callback: (Result<AnkiConnectorResult>) -> Unit
   ): Cancellable = toCallback(callback) {
       connector.pushDeck(deckName, markdown)
   }
    
   fun pullDeck(
       deckName: String,
       currentMarkdown: String,
       callback: (Result<AnkiConnectorResult>) -> Unit
   ): Cancellable = toCallback(callback) {
       connector.pullDeck(deckName, currentMarkdown)
   }
    
   fun <T> toCallback(
       callback: (Result<T>) -> Unit,
       body: suspend () -> T
   ): Cancellable {
       val job = scope.launch {
           try {
               val result = body()
               callback(Result.success(result))
           } catch (t: Throwable) {
               callback(Result.failure(t))
           }
       }
       return Cancellable(job)
   }
    
   class Cancellable(private val job: Job) {
       fun cancel() {
           job.cancel()
       }
   }
}
```


```
@JsExport
@JsName("AnkiConnector")
class AnkiConnectorJs {
   private val connector = AnkiConnector(/*...*/)
   private val scope = CoroutineScope(SupervisorJob())
    
   fun checkConnection(): Promise<Boolean> = scope.promise {
       connector.checkConnection()
   }
    
   fun getDeckNames(): Promise<Array<String>> =
       scope.promise {
           connector.getDeckNames().toTypedArray()
       }
  
   fun pushDeck(
       deckName: String,
       markdown: String
   ): Promise<AnkiConnectorResult> = scope.promise {
       connector.pushDeck(deckName, markdown)
   }
    
   fun pullDeck(
       deckName: String,
       currentMarkdown: String
   ): Promise<AnkiConnectorResult> = scope.promise {
       connector.pullDeck(deckName, currentMarkdown)
   }
}
```


```
class AnkiConnectorFuture {
    private val connector = AnkiConnector(/*...*/)
    private val scope = CoroutineScope(SupervisorJob())
    
    fun checkConnection(): CompletableFuture<Boolean> =
        scope.future {
            connector.checkConnection()
        }
    
    fun getDeckNames(): CompletableFuture<List<String>> =
        scope.future {
            connector.getDeckNames()
        }
    
    fun pushDeck(
        deckName: String,
        markdown: String
    ): CompletableFuture<AnkiConnectorResult> = scope.future {
        connector.pushDeck(deckName, markdown)
    }
    
    fun pullDeck(
        deckName: String,
        currentMarkdown: String
    ): CompletableFuture<AnkiConnectorResult> = scope.future {
        connector.pullDeck(deckName, currentMarkdown)
    }
}
```


```
class AnkiConnectorBlocking {
    private val connector = AnkiConnector(/*...*/)
    
    fun checkConnection(): Single<Boolean> = rxSingle {
        connector.checkConnection()
    }
    
    fun getDeckNames(): Single<List<String>> = rxSingle {
        connector.getDeckNames()
    }
    
    fun pushDeck(
        deckName: String,
        markdown: String
    ): Single<AnkiConnectorResult> = rxSingle {
        connector.pushDeck(deckName, markdown)
    }
    
    fun pullDeck(
        deckName: String,
        currentMarkdown: String
    ): Single<AnkiConnectorResult> = rxSingle {
        connector.pullDeck(deckName, currentMarkdown)
    }
}
```


```
// Java
public class MainJava {
   public static void main(String[] args) {
       AnkiConnector connector = new AnkiConnector();
       boolean connected;
       try {
           connected = BuildersKt.runBlocking(
                   EmptyCoroutineContext.INSTANCE,
                   (s, c) -> connector.checkConnection(c)
           );
       } catch (InterruptedException e) {
           throw new RuntimeException(e);
       }
       // ...
   }
}
```


```
// Java
class SuspendUtils {
   public static <T> T runBlocking(
       Function<Continuation<? super T>, T> func
   ) {
       try {
           return BuildersKt.runBlocking(
               EmptyCoroutineContext.INSTANCE,
               (s, c) -> func.apply(c)
           );
       } catch (InterruptedException e) {
           throw new RuntimeException(e);
       }
   }
}

public class MainJava {
   public static void main(String[] args) {
       AnkiConnector connector = new AnkiConnector();
       boolean connected = SuspendUtils.runBlocking((c) ->
           connector.checkConnection(c)
       );
       // ...
   }
}
```


```
suspend fun main() = coroutineScope {
   Flux.range(1, 5).asFlow()
       .collect { print(it) } // 12345
   Flowable.range(1, 5).asFlow()
       .collect { print(it) } // 12345
   Observable.range(1, 5).asFlow()
       .collect { print(it) } // 12345
}
```


```
suspend fun main(): Unit = coroutineScope {
    val flow = flowOf(1, 2, 3, 4, 5)
    
    flow.asFlux()
        .doOnNext { print(it) } // 12345
        .subscribe()
    
    flow.asFlowable()
        .subscribe { print(it) } // 12345
    
    flow.asObservable()
        .subscribe { print(it) } // 12345
}
```


```
// Kotlin
object FlowUtils {
   private val scope = CoroutineScope(SupervisorJob())
    
   @JvmStatic
   @JvmOverloads
   fun <T> observe(
       flow: Flow<T>,
       onNext: OnNext<T>? = null,
       onError: OnError? = null,
       onCompletion: OnCompletion? = null,
   ) {
       scope.launch {
           flow.onCompletion { onCompletion?.handle() }
               .onEach { onNext?.handle(it) }
               .catch { onError?.handle(it) }
               .collect()
       }
   }
    
   @JvmStatic
   @JvmOverloads
   fun <T> observeBlocking(
       flow: Flow<T>,
       onNext: OnNext<T>? = null,
       onError: OnError? = null,
       onCompletion: OnCompletion? = null,
   ) = runBlocking {
       flow.onCompletion { onCompletion?.handle() }
           .onEach { onNext?.handle(it) }
           .catch { onError?.handle(it) }
           .collect()
   }
    
   fun interface OnNext<T> {
       fun handle(value: T)
   }
    
   fun interface OnError {
       fun handle(value: Throwable)
   }
    
   fun interface OnCompletion {
       fun handle()
   }
}

class FlowTest {
   fun test(): Flow<String> = flow {
       emit("A")
       delay(1000)
       emit("B")
   }
}
```


```
// Java
public class Main {
    public static void main(String[] args) {
        FlowTest obj = new FlowTest();
        FlowUtils.observeBlocking(
                obj.test(),
                System.out::println
        );
    }
}
// A
// (1 sec)
// B
```