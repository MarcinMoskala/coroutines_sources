package f_209_state.s_19

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

suspend fun main() {
    val mutex = Mutex()
    println("Started")
    mutex.withLock("main()") {
        mutex.withLock("main()") {
            println("Will never be printed")
        }
    }
}
