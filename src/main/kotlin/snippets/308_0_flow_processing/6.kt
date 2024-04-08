package f_308_0_flow_processing.s_6

import kotlinx.coroutines.flow.flow

suspend fun main() {
    flow map@{ // 1
        flow flowOf@{ // 2
            for (element in arrayOf('a', 'b')) { // 3
                this@flowOf.emit(element) // 4
            }
        }.collect { value -> // 5
            this@map.emit(value.uppercase()) // 6
        }
    }.collect { // 7
        print(it) // 8
    }
}
