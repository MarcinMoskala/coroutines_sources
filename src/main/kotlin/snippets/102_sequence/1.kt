package f_102_sequence.s_1

val seq = sequence {
    yield(1)
    yield(2)
    yield(3)
}

fun main() {
    for (num in seq) {
        print(num)
    } // 123
}
