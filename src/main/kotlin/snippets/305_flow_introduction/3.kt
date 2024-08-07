package f_305_flow_introduction.s_3

fun getSequence(): Sequence<String> = sequence {
   repeat(3) {
       Thread.sleep(1000)
       yield("User$it")
   }
}

fun main() {
   val list = getSequence()
   println("Function started")
   list.forEach { println(it) }
}
