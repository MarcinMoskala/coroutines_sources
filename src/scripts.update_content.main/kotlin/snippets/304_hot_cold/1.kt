package f_304_hot_cold.s_1

import kotlin.*

fun main() {
    val l = buildList {
        repeat(3) {
            println("L: Adding User$it")
            add("User$it")
        }
    }

    val l2 = l.map {
        println("L: Processing $it")
        "Processed $it"
    }

    val s = sequence {
        repeat(3) {
            println("S: Adding User$it")
            yield("User$it")
        }
    }

    val s2 = s.map {
        println("S: Processing $it")
        "Processed $it"
    }
}
