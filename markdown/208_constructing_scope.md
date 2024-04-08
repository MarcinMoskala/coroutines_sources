```
interface CoroutineScope {
    val coroutineContext: CoroutineContext
}
```


```
class SomeClass : CoroutineScope {
   override val coroutineContext: CoroutineContext = Job()

   fun onStart() {
       launch {
           // ...
       }
   }
}
```


```
class SomeClass {
   val scope: CoroutineScope = ...

   fun onStart() {
       scope.launch {
           // ...
       }
   }
}
```


```
public fun CoroutineScope(
   context: CoroutineContext
): CoroutineScope =
   ContextScope(
       if (context[Job] != null) context
       else context + Job()
   )

internal class ContextScope(
   context: CoroutineContext
) : CoroutineScope {
   override val coroutineContext: CoroutineContext = context
   override fun toString(): String =
       "CoroutineScope(coroutineContext=$coroutineContext)"
}
```


```
class CacheRefreshService(
    private val userService: UserService,
    private val newsService: NewsService,
    private val backgroundScope: CoroutineScope,
) { 
    fun refresh() { 
        scope.launch { 
            userService.refresh() 
            newsService.refresh()
        } 
    }
}

class DataSyncManager(
    backgroundScope: CoroutineScope,
) {
    init {
        backgroundScope.launch {
            while (isActive) {
                retryOnFailure {
                    syncDataWithServer()
                    delay(syncIntervalMillis)
                }
            }
        }
    }

    // ...
}
```


```
@Configuration
public class CoroutineScopeConfiguration {

   @Bean
   fun coroutineDispatcher(): CoroutineDispatcher =
       Dispatchers.IO.limitedParallelism(50)

   @Bean
   fun coroutineExceptionHandler() {
       val logger = LoggerFactory.getLogger("DefaultHandler")
       CoroutineExceptionHandler { _, throwable ->
           logger.error("Unhandled exception", throwable)
       }
   }

   @Bean
   fun coroutineScope(
       coroutineDispatcher: CoroutineDispatcher,
       coroutineExceptionHandler: CoroutineExceptionHandler,
   ) = CoroutineScope(
       SupervisorJob() +
           coroutineDispatcher +
           coroutineExceptionHandler
   )
}
```


```
class DocumentsUpdater(
    val documentsFetcher: DocumentsFetcher,
) {
    private val disp = Dispatchers.IO.limitedParallelism(50)
    private val scope = CoroutineScope(SupervisorJob() + disp)
    private val documentsDir = File("documents")
    
    fun updateBooks() {
        scope.launch {
            val documents = documentsFetcher.fetch()
            documents.forEach { document ->
                val file = File(documentsDir, document.name)
                file.writeBytes(document.content)
            }
        }
    }
}
```


```
abstract class BaseViewModel : ViewModel() {
   private val _failure = MutableSharedFlow<Throwable>()
   val failure: SharedFlow<Throwable> = _failure

   private val context = Dispatchers.Main.immediate + 
           SupervisorJob() + 
           CoroutineExceptionHandler { _, throwable ->
                _failure.tryEmit(throwable) 
           }

   protected val scope = CoroutineScope(context)

   override fun onCleared() {
       context.cancelChildren()
   }
}
```


```
// Implementation from lifecycle-viewmodel-ktx version 2.4.0
public val ViewModel.viewModelScope: CoroutineScope
    get() {
        val scope: CoroutineScope? = this.getTag(JOB_KEY)
        if (scope != null) {
            return scope
        }
        return setTagIfAbsent(
            JOB_KEY,
            CloseableCoroutineScope(
                SupervisorJob() + Dispatchers.Main.immediate
            )
        )
    }

internal class CloseableCoroutineScope(
    context: CoroutineContext
) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}
```


```
class ArticlesListViewModel(
    private val produceArticles: ProduceArticlesUseCase,
) : ViewModel() {
    private val _progressVisible = MutableStateFlow(false)
    val progressBarVisible: StateFlow<Boolean> = _progressVisible

    private val _articlesListState =
        MutableStateFlow<ArticlesListState>(Initial)
    val articlesListState: StateFlow<ArticlesListState> =
        _articlesListState

    fun onCreate() {
        viewModelScope.launch {
            _progressVisible.value = true
            val articles = produceArticles.produce()
            _articlesListState.value = ArticlesLoaded(articles)
            _progressVisible.value = false
        }
    }
}
```