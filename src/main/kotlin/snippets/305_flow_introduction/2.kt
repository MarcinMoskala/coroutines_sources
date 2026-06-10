package f_305_flow_introduction.s_2

fun getList(): List<String> = List(3) {
   Thread.sleep(1000)
   "User$it"
}

fun main() {
   val list = getList()
   println("Function started")
   list.forEach { println(it) }
}
