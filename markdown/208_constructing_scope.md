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
abstract class BaseViewModel : ViewModel() {
    protected val scope = CoroutineScope(TODO())
}

class MainViewModel(
    private val userRepo: UserRepository,
    private val newsRepo: NewsRepository,
) : BaseViewModel {

    fun onCreate() {
        scope.launch {
            val user = userRepo.getUser()
            view.showUserData(user)
        }
        scope.launch {
            val news = newsRepo.getNews()
                .sortedByDescending { it.date }
            view.showNews(news)
        }
    }
}
```


```
abstract class BaseViewModel : ViewModel() {
   protected val scope = CoroutineScope(Dispatchers.Main)
}
```


```
abstract class BaseViewModel : ViewModel() {
   protected val scope =
       CoroutineScope(Dispatchers.Main + Job())

   override fun onCleared() {
       scope.cancel()
   }
}
```


```
abstract class BaseViewModel : ViewModel() {
   protected val scope =
       CoroutineScope(Dispatchers.Main + Job())

   override fun onCleared() {
       scope.coroutineContext.cancelChildren()
   }
}
```


```
abstract class BaseViewModel : ViewModel() {
   protected val scope =
       CoroutineScope(Dispatchers.Main + SupervisorJob())

   override fun onCleared() {
       scope.coroutineContext.cancelChildren()
   }
}
```


```
abstract class BaseViewModel(
   private val onError: (Throwable) -> Unit
) : ViewModel() {
   private val exceptionHandler =
       CoroutineExceptionHandler { _, throwable ->
           onError(throwable)
       }

   private val context =
       Dispatchers.Main + SupervisorJob() + exceptionHandler

   protected val scope = CoroutineScope(context)

   override fun onCleared() {
       context.cancelChildren()
   }
}
```


```
abstract class BaseViewModel : ViewModel() {
   private val _failure: MutableLiveData<Throwable> =
       MutableLiveData()
   val failure: LiveData<Throwable> = _failure

   private val exceptionHandler =
       CoroutineExceptionHandler { _, throwable ->
           _failure.value = throwable
       }

   private val context =
       Dispatchers.Main + SupervisorJob() + exceptionHandler

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
                SupervisorJob() +
                    Dispatchers.Main.immediate
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

    private val _progressBarVisible =
        MutableStateFlow(false)
    val progressBarVisible: StateFlow<Boolean> =
        _progressBarVisible

    private val _articlesListState =
        MutableStateFlow<ArticlesListState>(Initial)
    val articlesListState: StateFlow<ArticlesListState> =
        _articlesListState

    fun onCreate() {
        viewModelScope.launch {
            _progressBarVisible.value = true
            val articles = produceArticles.produce()
            _articlesListState.value =
                ArticlesLoaded(articles)
            _progressBarVisible.value = false
        }
    }
}
```


```
@Configuration
public class CoroutineScopeConfiguration {

   @Bean
   fun coroutineDispatcher(): CoroutineDispatcher =
       Dispatchers.IO.limitedParallelism(5)

   @Bean
   fun coroutineExceptionHandler() =
       CoroutineExceptionHandler { _, throwable ->
           FirebaseCrashlytics.getInstance()
               .recordException(throwable)
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
val analyticsScope = CoroutineScope(SupervisorJob())
```


```
private val exceptionHandler =
   CoroutineExceptionHandler { _, throwable ->
       FirebaseCrashlytics.getInstance()
           .recordException(throwable)
   }

val analyticsScope = CoroutineScope(
   SupervisorJob() + exceptionHandler
)
```


```
val analyticsScope = CoroutineScope(
   SupervisorJob() + Dispatchers.IO
)
```