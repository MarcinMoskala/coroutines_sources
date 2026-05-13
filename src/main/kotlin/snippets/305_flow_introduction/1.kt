package f_305_flow_introduction.s_1

interface Iterable<out T> {
   operator fun iterator(): Iterator<T>
}

interface Sequence<out T> {
   operator fun iterator(): Iterator<T>
}
