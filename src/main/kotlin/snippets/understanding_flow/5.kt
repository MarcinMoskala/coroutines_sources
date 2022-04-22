package f_understanding_flow.s_5

import kotlin.*

fun interface FlowCollector {
    fun emit(value: String)
}

interface Flow {
    fun collect(collector: FlowCollector)
}

fun main() {
    val builder: FlowCollector.() -> Unit = {
        emit("A")
        emit("B")
        emit("C")
    }
    val flow: Flow = object : Flow {
        override fun collect(collector: FlowCollector) {
            collector.builder()
        }
    }
    flow.collect { print(it) } // ABC
    flow.collect { print(it) } // ABC
}
