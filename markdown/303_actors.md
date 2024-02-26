```
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel

suspend fun massiveRun(action: suspend () -> Unit) =
    withContext(Dispatchers.Default) {
        List(1000) {
            launch {
                repeat(1000) { action() }
            }
        }
    }

//sampleStart
sealed class CounterMsg
object IncCounter : CounterMsg()
class GetCounter(
    val response: CompletableDeferred<Int>
) : CounterMsg()

fun CoroutineScope.counterActor(): Channel<CounterMsg> {
    val channel = Channel<CounterMsg>()
    launch {
        var counter = 0
        for (msg in channel) {
            when (msg) {
                is IncCounter -> {
                    counter++
                }
                is GetCounter -> {
                    msg.response.complete(counter)
                }
            }
        }
    }
    return channel
}

suspend fun main(): Unit = coroutineScope {
    val counter: SendChannel<CounterMsg> = counterActor()
    massiveRun { counter.send(IncCounter) }
    val response = CompletableDeferred<Int>()
    counter.send(GetCounter(response))
    println(response.await()) // 1000000
    counter.close()
}
//sampleEnd
```


```
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor

suspend fun massiveRun(action: suspend () -> Unit) = 
    withContext(Dispatchers.Default) {
        List(1000) {
            launch {
                repeat(1000) { action() }
            }
        }
    }

sealed class CounterMsg
object IncCounter : CounterMsg()
class GetCounter(
    val response: CompletableDeferred<Int>,
) : CounterMsg()

//sampleStart
fun CoroutineScope.counterActor() = actor<CounterMsg> {
    var counter = 0
    for (msg in channel) {
        when (msg) {
            is IncCounter -> counter++
            is GetCounter -> msg.response.complete(counter)
        }
    }
}
//sampleEnd

suspend fun main(): Unit = coroutineScope {
    val counter: SendChannel<CounterMsg> = counterActor()
    massiveRun { counter.send(IncCounter) }
    val response = CompletableDeferred<Int>()
    counter.send(GetCounter(response))
    println(response.await()) // 1000000
    counter.close()
}
```