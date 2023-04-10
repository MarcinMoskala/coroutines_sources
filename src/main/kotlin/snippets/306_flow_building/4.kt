package f_306_flow_building.s_4

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

suspend fun main() {
    val function = suspend {
        // this is suspending lambda expression
        delay(1000)
        "UserName"
    }

    function.asFlow()
        .collect { println(it) }
}
// (1 sec)
// UserName
