package f_305_understanding_flow.s_11

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

fun Flow<*>.counter() = flow<Int> {
    var counter = 0
    collect {
        counter++
        // to make it busy for a while
        List(100) { Random.nextLong() }.shuffled().sorted()
        emit(counter)
    }
}

suspend fun main(): Unit = coroutineScope {
    val f1 = List(1000) { "$it" }.asFlow()
    val f2 = List(1000) { "$it" }.asFlow()
        .counter()
    
    launch { println(f1.counter().last()) } // 1000
    launch { println(f1.counter().last()) } // 1000
    launch { println(f2.last()) } // 1000
    launch { println(f2.last()) } // 1000
}
