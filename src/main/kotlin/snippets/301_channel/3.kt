package f_301_channel.s_3

import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = produce {
        repeat(5) { index ->
            println("Producing next one")
            delay(1000)
            send(index * 2)
        }
    }

    for (element in channel) {
        println(element)
    }
}
//sampleEnd
