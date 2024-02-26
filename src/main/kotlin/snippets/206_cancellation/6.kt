package f_206_cancellation.s_6

import kotlinx.coroutines.*
import kotlin.random.Random

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            delay(Random.nextLong(2000))
            println("Done")
        } finally {
            print("Will always be printed")
        }
    }
    delay(1000)
    job.cancelAndJoin()
}
// Will always be printed
// (or)
// Done
// Will always be printed
//sampleEnd
