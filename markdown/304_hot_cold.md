```
//1
import kotlin.*

fun main() {
    val l = buildList {
        repeat(3) {
            println("L: Adding User$it")
            add("User$it")
        }
    }

    val l2 = l.map {
        println("L: Processing $it")
        "Processed $it"
    }

    val s = sequence {
        repeat(3) {
            println("S: Adding User$it")
            yield("User$it")
        }
    }

    val s2 = s.map {
        println("S: Processing $it")
        "Processed $it"
    }
}
// L: Added User0
// L: Added User1
// L: Added User2
// L: Processing User0
// L: Processing User1
// L: Processing User2
```


```
//2
import kotlin.*

fun m(i: Int): Int {
    print("m$i ")
    return i * i
}

fun f(i: Int): Boolean {
    print("f$i ")
    return i >= 10
}

fun main() {
    listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .map { m(it) }
        .find { f(it) }
        .let { print(it) }
    // m1 m2 m3 m4 m5 m6 m7 m8 m9 m10 f1 f4 f9 f16 16

    println()

    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .map { m(it) }
        .find { f(it) }
        .let { print(it) }
    // m1 f1 m2 f4 m3 f9 m4 f16 16
}
```


```
//3
import kotlin.*

fun m(i: Int): Int {
    print("m$i ")
    return i * i
}

fun main() {
    val l = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .map { m(it) } // m1 m2 m3 m4 m5 m6 m7 m8 m9 m10

    println(l) // [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
    println(l.find { it > 10 }) // 16
    println(l.find { it > 10 }) // 16
    println(l.find { it > 10 }) // 16

    val s = sequenceOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .map { m(it) }

    println(s.toList())
    // [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
    println(s.find { it > 10 }) // m1 m2 m3 m4 16
    println(s.find { it > 10 }) // m1 m2 m3 m4 16
    println(s.find { it > 10 }) // m1 m2 m3 m4 16
}
```


```
val channel = produce {
    while (true) {
        val x = computeNextValue()
        send(x)
    }
}

val flow = flow {
    while (true) {
        val x = computeNextValue()
        emit(x)
    }
}
```


```
//4
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

private fun CoroutineScope.makeChannel() = produce {
    println("Channel started")
    for (i in 1..3) {
        delay(1000)
        send(i)
    }
}

suspend fun main() = coroutineScope {
    val channel = makeChannel()

    delay(1000)
    println("Calling channel...")
    for (value in channel) {
        println(value)
    }
    println("Consuming again...")
    for (value in channel) {
        println(value)
    }
}
// Channel started
// (1 sec)
// Calling channel...
// 1
// (1 sec)
// 2
// (1 sec)
// 3
// Consuming again...
```


```
//5
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private fun makeFlow() = flow {
    println("Flow started")
    for (i in 1..3) {
        delay(1000)
        emit(i)
    }
}

suspend fun main() = coroutineScope {
    val flow = makeFlow()

    delay(1000)
    println("Calling flow...")
    flow.collect { value -> println(value) }
    println("Consuming again...")
    flow.collect { value -> println(value) }
}
// (1 sec)
// Calling flow...
// Flow started
// (1 sec)
// 1
// (1 sec)
// 2
// (1 sec)
// 3
// Consuming again...
// Flow started
// (1 sec)
// 1
// (1 sec)
// 2
// (1 sec)
// 3
```