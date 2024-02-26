package f_205_job.s_9

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    launch {
        delay(1000)
        println("Test1")
    }
    launch {
        delay(2000)
        println("Test2")
    }

    val children = coroutineContext[Job]
        ?.children

    val childrenNum = children?.count()
    println("Number of children: $childrenNum")
    children?.forEach { it.join() }
    println("All tests are done")
}
// Number of children: 2
// (1 sec)
// Test1
// (1 sec)
// Test2
// All tests are done
