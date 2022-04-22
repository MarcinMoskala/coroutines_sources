package f_understanding_flow.s_6

import kotlin.*

fun interface FlowCollector {
    fun emit(value: String)
}

interface Flow {
    fun collect(collector: FlowCollector)
}

fun flow(builder: FlowCollector.() -> Unit) = object : Flow {
    override fun collect(collector: FlowCollector) {
        collector.builder()
    }
}

fun main() {
    val f: Flow = flow {
        emit("A")
        emit("B")
        emit("C")
    }
    f.collect { print(it) } // ABC
    f.collect { print(it) } // ABC
}
