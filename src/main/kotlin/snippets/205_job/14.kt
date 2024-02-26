package f_205_job.s_14

import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) { // the new job replaces one from parent
        delay(1000)
        println("Text 1")
    }
    launch(job) { // the new job replaces one from parent
        delay(2000)
        println("Text 2")
    }
    job.complete()
    job.join()
}
// (1 sec)
// Text 1
// (1 sec)
// Text 2
//sampleEnd
