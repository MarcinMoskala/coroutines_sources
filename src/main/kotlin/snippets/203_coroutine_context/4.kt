package f_203_coroutine_context.s_4

import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.CoroutineContext

//sampleStart
fun main() {
    val ctx1: CoroutineContext = CoroutineName("Name1")
    println(ctx1[CoroutineName]?.name) // Name1

    val ctx2: CoroutineContext = CoroutineName("Name2")
    println(ctx2[CoroutineName]?.name) // Name2

    val ctx3 = ctx1 + ctx2
    println(ctx3[CoroutineName]?.name) // Name2
}
//sampleEnd
