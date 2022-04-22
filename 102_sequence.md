```
val seq = sequence {
   yield(1)
   yield(2)
   yield(3)
}
```


```
fun main() {
   for (num in seq) {
       print(num)
   } // 123
}
```


```
//1
import kotlin.*

//sampleStart
val seq = sequence {
   println("Generating first")
   yield(1)
   println("Generating second")
   yield(2)
   println("Generating third")
   yield(3)
   println("Done")
}

fun main() {
   for (num in seq) {
       println("The next number is $num")
   }
}
// Generating first
// The next number is 1
// Generating second
// The next number is 2
// Generating third
// The next number is 3
// Done
//sampleEnd
```


```
//2
import kotlin.*

//sampleStart
val seq = sequence {
   println("Generating first")
   yield(1)
   println("Generating second")
   yield(2)
   println("Generating third")
   yield(3)
   println("Done")
}

fun main() {
   val iterator = seq.iterator()
   println("Starting")
   val first = iterator.next()
   println("First: $first")
   val second = iterator.next()
   println("Second: $second")
   // ...
}

// Prints:
// Starting
// Generating first
// First: 1
// Generating second
// Second: 2
//sampleEnd
```


```
//3
import java.math.BigInteger

//sampleStart
val fibonacci: Sequence<BigInteger> = sequence {
   var first = 0.toBigInteger()
   var second = 1.toBigInteger()
   while (true) {
       yield(first)
       val temp = first
       first += second
       second = temp
   }
}

fun main() {
   print(fibonacci.take(10).toList())
}
// [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
//sampleEnd
```


```
fun randomNumbers(
   seed: Long = System.currentTimeMillis()
): Sequence<Int> = sequence {
   val random = Random(seed)
   while (true) {
       yield(random.nextInt())
   }
}

fun randomUniqueStrings(
   length: Int,
   seed: Long = System.currentTimeMillis()
): Sequence<String> = sequence {
   val random = Random(seed)
   val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
   while (true) {
       val randomString = (1..length)
           .map { i -> random.nextInt(charPool.size) }
           .map(charPool::get)
           .joinToString("");
       yield(randomString)
   }
}.distinct()
```


```
fun allUsersFlow(
   api: UserApi
): Flow<User> = flow {
   var page = 0
   do {
       val users = api.takePage(page++) // suspending
       emitAll(users)
   } while (!users.isNullOrEmpty())
}
```