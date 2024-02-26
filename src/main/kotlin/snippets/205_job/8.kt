package f_205_job.s_8

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val job1 = launch {
        delay(1000)
        println("Test1")
    }
    val job2 = launch {
        delay(2000)
        println("Test2")
    }

    job1.join()
    job2.join()
    println("All tests are done")
}
// (1 sec)
// Test1
// (1 sec)
// Test2
// All tests are done
