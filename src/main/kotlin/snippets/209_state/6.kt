package f_209_state.s_6

import kotlin.concurrent.thread

@Volatile
var number: Int = 0

@Volatile
var ready: Boolean = false

fun main() {
    thread {
        while (!ready) {
            Thread.yield()
        }
        println(number)
    }
    number = 42
    ready = true
}
