```
// Simplified implementation of retryWhen
fun <T> Flow<T>.retryWhen(
    predicate: suspend FlowCollector<T>.(
        cause: Throwable,
        attempt: Long,
    ) -> Boolean,
): Flow<T> = flow {
        var attempt = 0L
        do {
            val shallRetry = try {
                collect { emit(it) }
                false
            } catch (e: Throwable) {
                predicate(e, attempt++)
                    .also { if (!it) throw e }
            }
        } while (shallRetry)
    }
```


```
// Actual implementation of retry
fun <T> Flow<T>.retry(
    retries: Long = Long.MAX_VALUE,
    predicate: suspend (cause: Throwable) -> Boolean = {true}
): Flow<T> {
    require(retries > 0) {
      "Expected positive amount of retries, but had $retries"
    }
    return retryWhen { cause, attempt ->
        attempt < retries && predicate(cause)
    }
}
```


```
suspend fun main() {
    flow {
        emit(1)
        emit(2)
        error("E")
        emit(3)
    }.retry(3) {
        print(it.message)
        true
    }.collect { print(it) } // 12E12E12E12(exception thrown)
}
```


```
fun makeConnection(config: ConnectionConfig) = api
    .startConnection(config)
    .retry { e ->
        delay(1000)
        log.error(e) { "Error for $config" }
        true
    }
```


```
fun makeConnection(config: ConnectionConfig) = api
    .startConnection(config)
    .retryWhen { e, attempt ->
        delay(100 * attempt)
        log.error(e) { "Error for $config" }
        e is ApiException && e.code !in 400..499
    }
```


```
// Simplified distinctUntilChanged implementation
fun <T> Flow<T>.distinctUntilChanged(): Flow<T> = flow {
        var previous: Any? = NOT_SET
        collect {
            if (previous == NOT_SET || previous != it) {
                emit(it)
                previous = it
            }
        }
    }

private val NOT_SET = Any()
```


```
suspend fun main() {
    flowOf(1, 2, 2, 3, 2, 1, 1, 3)
        .distinctUntilChanged()
        .collect { print(it) } // 123213
}
```


```
data class User(val id: Int, val name: String) {
    override fun toString(): String = "[$id] $name"
}

suspend fun main() {
    val users = flowOf(
        User(1, "Alex"),
        User(1, "Bob"),
        User(2, "Bob"),
        User(2, "Celine")
    )
    
    println(users.distinctUntilChangedBy { it.id }.toList())
    // [[1] Alex, [2] Bob]
    println(users.distinctUntilChangedBy{ it.name }.toList())
    // [[1] Alex, [1] Bob, [2] Celine]
    println(users.distinctUntilChanged { prev, next ->
        prev.id == next.id || prev.name == next.name
    }.toList()) // [[1] Alex, [2] Bob]
    // [2] Bob was emitted,
    // because we compare to the previous emitted
}
```