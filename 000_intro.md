```
suspend fun main(): Unit = coroutineScope {
   launch {
       delay(1000L)
       println("World!")
   }
   println("Hello,")
}
// Hello,
// (1 sec)
// World!
```


```
launch(CoroutineName("Name1")) { ... }
launch(CoroutineName("Name2") + Job()) { ... }
```


```
suspend fun main(): Unit = coroutineScope {
   println("Hello,") // Hello,
   delay(1000L) // (1 sec)
   println("World!") // World!
}
```


```
suspend fun main(): Unit = coroutineScope {
    println("Hello,") // 1
    delay(1000L) // 2
    println("World!") // 3
}
```