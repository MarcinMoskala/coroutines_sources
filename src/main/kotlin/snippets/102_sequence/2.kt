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
   val iterator = seq.iterator()
   println("Starting")
   val first = iterator.next()
   println("First: $first")
   val second = iterator.next()
   println("Second: $second")
   // ...
}

// Prints:
// Starting
// Generating first
// First: 1
// Generating second
// Second: 2
//sampleEnd
