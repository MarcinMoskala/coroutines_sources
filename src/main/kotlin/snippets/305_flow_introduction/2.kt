package f_305_flow_introduction.s_2

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
// Function started
// (1 sec)
// User0
// (1 sec)
// User1
// (1 sec)
// User2
