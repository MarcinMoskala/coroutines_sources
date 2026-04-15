package f_205_job.s_2

import kotlinx.coroutines.*
import kotlin.coroutines.*

val CoroutineContext.job: Job
    get() = get(Job) ?: error("Current context doesn't...")

fun main(): Unit = runBlocking {
    print(coroutineContext.job.isActive) // true
}
