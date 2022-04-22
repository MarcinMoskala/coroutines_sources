package f_306_flow_building.s_7

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest

fun main() = runTest {
   flow { // 1
       emit("A")
       emit("B")
       emit("C")
   }.collect { value -> // 2
       println(value)
   }
}
// A
// B
// C
