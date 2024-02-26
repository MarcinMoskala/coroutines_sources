package f_205_job.s_13

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Error

//sampleStart
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
        job.completeExceptionally(Error("Some error"))
    }

    job.join()

    launch(job) {
        println("Will not be printed")
    }

    println("Done")
}
// Rep0
// Rep1
// Done
//sampleEnd
