```
//1
// (1 sec)
// onEach A
// (1 sec)
// collect A
// onEach B
// (1 sec)
// collect B
// onEach C
// (1 sec)
// collect C
```


```
fun observeChannel(channelId: String) = service
    .messagesFlow(channelId)
    .buffer(64)
    .map { it.toNetworkMessage() }
```


```
//2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow

suspend fun main() {
   val flow = flow {
       for (i in 1..10) {
           delay(300)
           emit(i)
       }
   }
    
    flow.collect {
        delay(1000)
        print("$it, ")
    }
    // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
    
    flow.conflate().collect {
        delay(1000)
        print("$it, ")
    }
    // 1, 4, 7, 10,
}
```


```
//3
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

suspend fun main() {
    flow {
        emit(1)
        delay(400)
        emit(2)
        delay(600)
        emit(3)
        delay(1000)
        emit(4)
        delay(1000)
        emit(5)
    }
        .debounce(800)
        .collect { println(it) }
}
// (0.4 + 0.6 + 0.8 = 1.8 sec)
// 3
// (1 sec)
// 4
// (0.2 sec)
// 5
```


```
fun searchResults() = searchQueryFlow
    .debounce(300.milliseconds)    
    .flatMapLatest { query -> 
        fetchSearchResults(query)
    }
```


```
//4
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample

suspend fun main() {
    flow {
        repeat(9) { // Only the last element will be received
            delay(100)
            emit(it)
        }
        delay(1000)
        emit(10) // This element will be received
        delay(200)
        emit(11) // This element will be received
        delay(1000)
        emit(12) // This element will be ignored 
        // due to flow completion
    }.sample(1000)
        .collect { println(it) }
}
// (1 sec)
// 8
// (1 sec)
// 10
// (1 sec)
// 11
```