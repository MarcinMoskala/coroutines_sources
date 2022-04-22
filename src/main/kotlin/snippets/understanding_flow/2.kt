package f_understanding_flow.s_2

import kotlin.*

fun main() {
    val f: ((String) -> Unit) -> Unit = { emit -> // 1
        emit("A")
        emit("B")
        emit("C")
    }
    f { print(it) } // ABC
    f { print(it) } // ABC
}
