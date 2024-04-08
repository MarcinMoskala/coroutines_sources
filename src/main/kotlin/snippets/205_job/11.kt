package f_205_job.s_11

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val job = Job()

    launch(job) {
        repeat(5) { num ->
            delay(200)
            println("Rep$num")
        }
    }

    launch {
        delay(500)
        job.complete()
    }

    job.join()

    launch(job) {
        println("Will not be printed")
    }

    println("Done")
}
