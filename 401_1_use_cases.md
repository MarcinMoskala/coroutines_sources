```
// Retrofit
class GithubApi {
    @GET("orgs/{organization}/repos?per_page=100")
    suspend fun getOrganizationRepos(
        @Path("organization") organization: String
    ): List<Repo>
}
```


```
// Room
@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(location: Location)

    @Query("DELETE FROM location_table")
    suspend fun deleteLocations()

    @Query("SELECT * FROM location_table ORDER BY time")
    fun observeLocations(): Flow<List<Location>>
}
```


```
suspend fun requestNews(): News {
    return suspendCancellableCoroutine<News> { cont ->
        val call = requestNewsApi { news ->
            cont.resume(news)
        }
        cont.invokeOnCancellation {
            call.cancel()
        }
    }
}
```


```
suspend fun requestNews(): Result<News> {
    return suspendCancellableCoroutine<News> { cont ->
        val call = requestNewsApi(
            onSuccess = { news -> 
                cont.resume(Result.success(news))
            },
            onError = { e -> 
                cont.resume(Result.failure(e)) 
            }
        )
        cont.invokeOnCancellation {
            call.cancel()
        }
    }
}
```


```
suspend fun requestNews(): News? {
    return suspendCancellableCoroutine<News> { cont ->
        val call = requestNewsApi(
            onSuccess = { news -> cont.resume(news) },
            onError = { e -> cont.resume(null) }
        )
        cont.invokeOnCancellation {
            call.cancel()
        }
    }
}
```


```
suspend fun requestNews(): News {
    return suspendCancellableCoroutine<News> { cont ->
        val call = requestNewsApi(
            onSuccess = { news -> cont.resume(news) },
            onError = { e -> cont.resumeWithException(e) }
        )
        cont.invokeOnCancellation {
            call.cancel()
        }
    }
}
```


```
class DiscSaveRepository(
    private val discReader: DiscReader
) : SaveRepository {

    override suspend fun loadSave(name: String): SaveData =
        withContext(Dispatchers.IO) {
            discReader.read("save/$name")
        }
}
```


```
class LibraryGoogleAccountVerifier : GoogleAccountVerifier {
    private val dispatcher = Dispatchers.IO
        .limitedParallelism(100)

    private var verifier =
        GoogleIdTokenVerifier.Builder(..., ...)
    .setAudience(...)
    .build()

    override suspend fun getUserData(
        googleToken: String
    ): GoogleUserData? = withContext(dispatcher) {
        verifier.verify(googleToken)
            ?.payload
            ?.let {
                GoogleUserData(
                    email = it.email,
                    name = it.getString("given_name"),
                    surname = it.getString("family_name"),
                    imageUrl = it.getString("picture"),
                )
            }
    }
}

```


```
class CertificateGenerator {
    private val dispatcher = Dispatchers.IO
        .limitedParallelism(5)

    suspend fun generate(data: CertificateData): UserData =
        withContext(dispatcher) {
            Runtime.getRuntime()
                .exec("generateCertificate " + data.toArgs())
        }
}
```


```
suspend fun calculateModel() =
    withContext(Dispatchers.Default) {
        model.fit(
            dataset = newTrain,
            epochs = 10,
            batchSize = 100,
            verbose = false
        )
    }
```


```
suspend fun setUserName(name: String) =
    withContext(Dispatchers.Main.immediate) {
        userNameView.text = name
    }
```


```
// Room
@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(location: Location)

    @Query("DELETE FROM location_table")
    suspend fun deleteLocations()

    @Query("SELECT * FROM location_table ORDER BY time")
    fun observeLocations(): Flow<List<Location>>
}
```


```
fun listenMessages(): Flow<List<Message>> = callbackFlow {
    socket.on("NewMessage") { args ->
        trySend(args.toMessage())
    }
    awaitClose()
}
```


```
fun EditText.listenTextChange(): Flow<String> = callbackFlow {
    val watcher = doAfterTextChanged {
        trySendBlocking(it.toString())
    }
    awaitClose { removeTextChangedListener(watcher) }
}
```


```
fun flowFrom(api: CallbackBasedApi): Flow<T> = callbackFlow {
    val callback = object : Callback {
        override fun onNextValue(value: T) {
            trySendBlocking(value)
        }
        override fun onApiError(cause: Throwable) {
            cancel(CancellationException("API Error", cause))
        }
        override fun onCompleted() = channel.close()
    }
    api.register(callback)
    awaitClose { api.unregister(callback) }
}
```


```
fun fibonacciFlow(): Flow<BigDecimal> = flow {
    var a = BigDecimal.ZERO
    var b = BigDecimal.ONE
    emit(a)
    emit(b)
    while (true) {
        val temp = a
        a = b
        b += temp
        emit(b)
    }
}.flowOn(Dispatchers.Default)

fun filesContentFlow(path: String): Flow<String> =
    channelFlow {
        File(path).takeIf { it.exists() }
            ?.listFiles()
            ?.forEach {
                send(it.readText())
            }
    }.flowOn(Dispatchers.IO)
```