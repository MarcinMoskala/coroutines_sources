```
//1
import kotlinx.coroutines.flow.*

suspend fun main() {
    val flow = flowOf(1, 2, 3, 4) // [1, 2, 3, 4]
        .map { it * it } // [1, 4, 9, 16]

    println(flow.first()) // 1
    println(flow.count()) // 4

    println(flow.reduce { acc, value -> acc * value }) // 576
    println(flow.fold(0) { acc, value -> acc + value }) // 30
}
```


```
suspend fun Flow<Int>.sum(): Int {
    var sum = 0
    collect { value ->
        sum += value
    }
    return sum
}
```