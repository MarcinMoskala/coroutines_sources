package f_205_job.s_6

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val parentJob: Job = coroutineContext.job
    val job: Job = launch {
        delay(1000)
    }

    println(job == parentJob) // false
    println(parentJob.children.first() == job) // true
    println(job.parent == parentJob) // true
}
