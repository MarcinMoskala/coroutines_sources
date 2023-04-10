package f_304_hot_cold.s_1

import kotlin.*

//sampleStart
fun main() {
    val l = buildList {
        repeat(3) {
            add("User$it")
            println("L: Added User")
        }
    }

    val l2 = l.map {
        println("L: Processing")
        "Processed $it"
    }

    val s = sequence {
        repeat(3) {
            yield("User$it")
            println("S: Added User")
        }
    }

    val s2 = s.map {
        println("S: Processing")
        "Processed $it"
    }
}
// L: Added User
// L: Added User
// L: Added User
// L: Processing
// L: Processing
// L: Processing
//sampleEnd
