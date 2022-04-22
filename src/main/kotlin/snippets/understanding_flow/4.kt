package f_understanding_flow.s_4

import kotlin.*

fun interface FlowCollector {
    fun emit(value: String)
}

fun main() {
    val f: FlowCollector.() -> Unit = {
        emit("A")
        emit("B")
        emit("C")
    }
    f { print(it) } // ABC
    f { print(it) } // ABC
}
