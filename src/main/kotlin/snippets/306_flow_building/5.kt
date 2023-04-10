package f_306_flow_building.s_5

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

suspend fun getUserName(): String {
    delay(1000)
    return "UserName"
}

suspend fun main() {
    ::getUserName
        .asFlow()
        .collect { println(it) }
}
// (1 sec)
// UserName
