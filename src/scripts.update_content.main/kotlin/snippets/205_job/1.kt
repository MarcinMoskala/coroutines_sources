package f_205_job.s_1

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    print(coroutineContext[Job]?.isActive) // true
}
