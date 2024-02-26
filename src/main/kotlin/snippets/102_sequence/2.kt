package f_102_sequence.s_2

import kotlin.*

//sampleStart
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
// Generating first
// The next number is 1
// Generating second
// The next number is 2
// Generating third
// The next number is 3
// Done
//sampleEnd
