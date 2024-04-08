```
//1
import kotlinx.coroutines.flow.*

suspend fun main() {
    flowOf(1, 2, 3) // [1, 2, 3]
        .map { it * it } // [1, 4, 9]
        .collect { print(it) } // 149
}
```


```
fun <T, R> Flow<T>.map(
    transform: suspend (value: T) -> R
): Flow<R> = flow { // here we create a new flow
    collect { value -> // here we collect from receiver
        emit(transform(value))
    }
}
```


```
// Here we use map to have user actions from input events
fun actionsFlow(): Flow<UserAction> =
    observeInputEvents()
        .map { toAction(it.code) }

// Here we use map to convert from User to UserJson
fun getAllUser(): Flow<UserJson> =
    userRepository.getAllUsers()
        .map { it.toUserJson() }
```


```
//2
import kotlinx.coroutines.flow.*

suspend fun main() {
    (1..10).asFlow() // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        .filter { it <= 5 } // [1, 2, 3, 4, 5]
        .filter { isEven(it) } // [2, 4]
        .collect { print(it) } // 24
}

fun isEven(num: Int): Boolean = num % 2 == 0
```


```
fun <T> Flow<T>.filter(
    predicate: suspend (T) -> Boolean
): Flow<T> = flow { // here we create a new flow
    collect { value -> // here we collect from receiver
        if (predicate(value)) {
            emit(value)
        }
    }
}
```


```
// Here we use filter to drop invalid actions
fun actionsFlow(): Flow<UserAction> =
    observeInputEvents()
        .filter { isValidAction(it.code) }
        .map { toAction(it.code) }
```


```
//3
import kotlinx.coroutines.flow.*

suspend fun main() {
    ('A'..'Z').asFlow()
        .take(5) // [A, B, C, D, E]
        .collect { print(it) } // ABCDE
}
```


```
//4
import kotlinx.coroutines.flow.*

suspend fun main() {
    ('A'..'Z').asFlow()
        .drop(20) // [U, V, W, X, Y, Z]
        .collect { print(it) } // UVWXYZ
}
```


```
//5
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

suspend fun main() {
    flowOf('a', 'b')
        .map { it.uppercase() }
        .collect { print(it) } // AB
}

fun <T, R> Flow<T>.map(
    transform: suspend (value: T) -> R
): Flow<R> = flow {
    collect { value ->
        emit(transform(value))
    }
}

fun <T> flowOf(vararg elements: T): Flow<T> = flow {
    for (element in elements) {
        emit(element)
    }
}
```


```
//6
import kotlinx.coroutines.flow.flow

suspend fun main() {
    flow map@{ // 1
        flow flowOf@{ // 2
            for (element in arrayOf('a', 'b')) { // 3
                this@flowOf.emit(element) // 4
            }
        }.collect { value -> // 5
            this@map.emit(value.uppercase()) // 6
        }
    }.collect { // 7
        print(it) // 8
    }
}
```