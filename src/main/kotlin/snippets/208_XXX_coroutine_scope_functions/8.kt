package f_208_XXX_coroutine_scope_functions.s_8

import kotlinx.coroutines.*

//sampleStart
fun main() = runBlocking {
    println("Before")

    supervisorScope {
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
// (1 sec)
// Done
// After
//sampleEnd
