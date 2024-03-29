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
//1
import kotlinx.coroutines.flow.*

suspend fun main() {
    flowOf(1, 2, 2, 3, 2, 1, 1, 3)
        .distinctUntilChanged()
        .collect { print(it) } // 123213
}
```


```
//2
import kotlinx.coroutines.flow.*

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