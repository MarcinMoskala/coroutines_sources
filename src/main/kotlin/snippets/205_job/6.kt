package f_205_job.s_6

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val job: Job = launch {
        delay(1000)
    }

    val parentJob: Job = coroutineContext.job
    // or coroutineContext[Job]!!
    println(job == parentJob) // false
    val parentChildren: Sequence<Job> = parentJob.children
    println(parentChildren.first() == job) // true
}
