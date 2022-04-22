```
//1
import kotlin.*

fun main() {
    val f: () -> Unit = {
        print("A")
        print("B")
        print("C")
    }
    f() // ABC
    f() // ABC
}
```


```
//2
import kotlin.*

fun main() {
    val f: ((String) -> Unit) -> Unit = { emit -> // 1
        emit("A")
        emit("B")
        emit("C")
    }
    f { print(it) } // ABC
    f { print(it) } // ABC
}
```


```
//3
import kotlin.*

fun interface FlowCollector {
    fun emit(value: String)
}

fun main() {
    val f: (FlowCollector) -> Unit = {
        it.emit("A")
        it.emit("B")
        it.emit("C")
    }
    f { print(it) } // ABC
    f { print(it) } // ABC
}
```


```
//4
import kotlin.*

fun interface FlowCollector {
    fun emit(value: String)
}

fun main() {
    val f: FlowCollector.() -> Unit = {
        emit("A")
        emit("B")
        emit("C")
    }
    f { print(it) } // ABC
    f { print(it) } // ABC
}
```


```
//5
import kotlin.*

fun interface FlowCollector {
    fun emit(value: String)
}

interface Flow {
    fun collect(collector: FlowCollector)
}

fun main() {
    val builder: FlowCollector.() -> Unit = {
        emit("A")
        emit("B")
        emit("C")
    }
    val flow: Flow = object : Flow {
        override fun collect(collector: FlowCollector) {
            collector.builder()
        }
    }
    flow.collect { print(it) } // ABC
    flow.collect { print(it) } // ABC
}
```


```
//6
import kotlin.*

fun interface FlowCollector {
    fun emit(value: String)
}

interface Flow {
    fun collect(collector: FlowCollector)
}

fun flow(builder: FlowCollector.() -> Unit) = object : Flow {
    override fun collect(collector: FlowCollector) {
        collector.builder()
    }
}

fun main() {
    val f: Flow = flow {
        emit("A")
        emit("B")
        emit("C")
    }
    f.collect { print(it) } // ABC
    f.collect { print(it) } // ABC
}
```


```
//7
import kotlin.*

fun interface FlowCollector<T> {
    suspend fun emit(value: T)
}

interface Flow<T> {
    suspend fun collect(collector: FlowCollector<T>)
}

fun <T> flow(builder: suspend FlowCollector<T>.() -> Unit) = object : Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        collector.builder()
    }
}

suspend fun main() {
    val f: Flow<String> = flow {
        emit("A")
        emit("B")
        emit("C")
    }
    f.collect { print(it) } // ABC
    f.collect { print(it) } // ABC
}
```


```
public fun <T> Iterator<T>.asFlow(): Flow<T> = flow {
    forEach { value ->
        emit(value)
    }
}

public fun <T> Sequence<T>.asFlow(): Flow<T> = flow {
    forEach { value ->
        emit(value)
    }
}

public fun <T> flowOf(vararg elements: T): Flow<T> = flow {
    for (element in elements) {
        emit(element)
    }
}
```


```
fun <T, R> Flow<T>.map(transformation: suspend (T) -> R): Flow<R> = flow {
    collect {
        emit(transformation(it))
    }
}
```


```
fun <T> Flow<T>.filter(predicate: suspend (T) -> Boolean): Flow<T> = flow {
    collect {
        if(predicate(it)) {
            emit(it)
        }
    }
}
```


```
fun <T> Flow<T>.onEach(action: suspend (T) -> Unit): Flow<T> = flow {
    collect {
        action(it)
        emit(it)
    }
}

// simplified implementation
fun <T> Flow<T>.onStart(action: suspend () -> Unit): Flow<T> = flow {
    action()
    collect {
        emit(it)
    }
}
```