package f_208_XXX_coroutine_scope_functions.s_9

import kotlinx.coroutines.*

//sampleStart
fun main() = runBlocking {
    println("Before")

    withContext(SupervisorJob()) {
        launch {
            delay(1000)
            throw Error()
        }

        launch {
            delay(2000)
            println("Done")
        }
    }

    println("After")
}
// Before
// (1 sec)
// Exception...
//sampleEnd
