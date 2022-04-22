package f_301_channel.s_2

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) { index ->
            println("Producing next one")
            delay(1000)
            channel.send(index * 2)
        }
        channel.close()
    }

    launch {
        for (element in channel) {
            println(element)
        }
        // or
        // channel.consumeEach { element ->
        //     println(element)
        // }
    }
}
//sampleEnd
