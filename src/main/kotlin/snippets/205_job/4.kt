package f_205_job.s_4

import kotlinx.coroutines.*

//sampleStart
fun main(): Unit = runBlocking {
    val deferred: Deferred<String> = async {
        delay(1000)
        "Test"
    }
    val job: Job = deferred
}
//sampleEnd
