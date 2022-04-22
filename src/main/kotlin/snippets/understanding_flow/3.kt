package f_understanding_flow.s_3

import kotlin.*

fun interface FlowCollector {
    fun emit(value: String)
}

fun main() {
    val f: (FlowCollector) -> Unit = {
        it.emit("A")
        it.emit("B")
        it.emit("C")
    }
    f { print(it) } // ABC
    f { print(it) } // ABC
}
