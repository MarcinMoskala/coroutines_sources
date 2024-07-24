package f_209_state.s_12

import kotlin.concurrent.thread

val lock1 = Any()
val lock2 = Any()

fun f1() = synchronized(lock1) {
    Thread.sleep(1000L)
    synchronized(lock2) {
        println("f1")
    }
}

fun f2() = synchronized(lock2) {
    Thread.sleep(1000L)
    synchronized(lock1) {
        println("f2")
    }
}

fun main() {
    thread { f1() }
    thread { f2() }
}
