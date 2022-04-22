package f_209_state.s_12

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

//sampleStart
suspend fun main() {
    val mutex = Mutex()
    println("Started")
    mutex.withLock {
        mutex.withLock {
            println("Will never be printed")
        }
    }
}
// Started
// (runs forever)
//sampleEnd
