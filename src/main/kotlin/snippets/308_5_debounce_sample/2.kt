package f_308_5_debounce_sample.s_2

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow

suspend fun main() {
   val flow = flow {
       for (i in 1..10) {
           delay(300)
           emit(i)
       }
   }
    
    flow.collect {
        delay(1000)
        print("$it, ")
    }
    // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
    
    flow.conflate().collect {
        delay(1000)
        print("$it, ")
    }
    // 1, 4, 7, 10,
}
