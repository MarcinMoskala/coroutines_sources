package f_102_sequence.s_2

import kotlin.*

val seq = sequence {
    println("Generating first")
    yield(1)
    println("Generating second")
    yield(2)
    println("Generating third")
    yield(3)
    println("Done")
}

fun main() {
    for (num in seq) {
        println("The next number is $num")
    }
}
