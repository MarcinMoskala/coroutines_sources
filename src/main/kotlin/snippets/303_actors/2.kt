package f_303_actors.s_2

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

fun CoroutineScope.counterActor() = actor<CounterMsg> {
    var counter = 0
    for (msg in channel) {
        when (msg) {
            is IncCounter -> counter++
            is GetCounter -> msg.response.complete(counter)
        }
    }
}


suspend fun main(): Unit = coroutineScope {
    val counter: SendChannel<CounterMsg> = counterActor()
    massiveRun { counter.send(IncCounter) }
    val response = CompletableDeferred<Int>()
    counter.send(GetCounter(response))
    println(response.await()) // 1000000
    counter.close()
}
