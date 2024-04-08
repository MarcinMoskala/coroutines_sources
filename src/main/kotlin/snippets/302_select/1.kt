package f_302_select.s_1

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

suspend fun requestData1(): String {
    delay(100_000)
    return "Data1"
}

suspend fun requestData2(): String {
    delay(1000)
    return "Data2"
}

suspend fun askMultipleForData(): String = coroutineScope {
    select<String> {
        async { requestData1() }.onAwait { it }
        async { requestData2() }.onAwait { it }
    }.also { coroutineContext.cancelChildren() }
}

suspend fun main(): Unit = coroutineScope {
    println(askMultipleForData())
}
