package f_202_coroutine_context.s_12

import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.test.assertEquals

data class User(val id: String, val name: String)

abstract class UuidProviderContext :
   CoroutineContext.Element {
  
   abstract fun nextUuid(): String

   override val key: CoroutineContext.Key<*> = Key

   companion object Key :
       CoroutineContext.Key<UuidProviderContext>
}

class RealUuidProviderContext : UuidProviderContext() {
   override fun nextUuid(): String =
       UUID.randomUUID().toString()
}

class FakeUuidProviderContext(
   private val fakeUuid: String
) : UuidProviderContext() {
   override fun nextUuid(): String = fakeUuid
}

suspend fun nextUuid(): String =
   checkNotNull(coroutineContext[UuidProviderContext]) {
       "UuidProviderContext not present" }
       .nextUuid()

// function under test
suspend fun makeUser(name: String) = User(
   id = nextUuid(),
   name = name
)

suspend fun main(): Unit {
   // production case
   withContext(RealUuidProviderContext()) {
       println(makeUser("Michał"))
       // e.g. User(id=d260482a-..., name=Michał)
   }

   // test case
   withContext(FakeUuidProviderContext("FAKE_UUID")) {
       val user = makeUser("Michał")
       println(user) // User(id=FAKE_UUID, name=Michał)
       assertEquals(User("FAKE_UUID", "Michał"), user)
   }
}
