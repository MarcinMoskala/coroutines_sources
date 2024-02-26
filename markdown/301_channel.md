```
interface SendChannel<in E> {
    suspend fun send(element: E)
    fun close(): Boolean
    //...
}

interface ReceiveChannel<out E> {
    suspend fun receive(): E
    fun cancel(cause: CancellationException? = null)
    // ...
}

interface Channel<E> : SendChannel<E>, ReceiveChannel<E>
```


```
//1
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) { index ->
            delay(1000)
            println("Producing next one")
            channel.send(index * 2)
        }
    }

    launch {
        repeat(5) {
            val received = channel.receive()
            println(received)
        }
    }
}
// (1 sec)
// Producing next one
// 0
// (1 sec)
// Producing next one
// 2
// (1 sec)
// Producing next one
// 4
// (1 sec)
// Producing next one
// 6
// (1 sec)
// Producing next one
// 8
//sampleEnd
```


```
//2
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) { index ->
            println("Producing next one")
            delay(1000)
            channel.send(index * 2)
        }
        channel.close()
    }

    launch {
        for (element in channel) {
            println(element)
        }
        // or
        // channel.consumeEach { element ->
        //     println(element)
        // }
    }
}
//sampleEnd
```


```
// This function produces a channel with
// next positive integers from 0 to `max`
fun CoroutineScope.produceNumbers(
   max: Int
): ReceiveChannel<Int> = produce {
       var x = 0
       while (x < 5) send(x++)
   }
```


```
//3
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = produce {
        repeat(5) { index ->
            println("Producing next one")
            delay(1000)
            send(index * 2)
        }
    }

    for (element in channel) {
        println(element)
    }
}
//sampleEnd
```


```
//4
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = produce(capacity = Channel.UNLIMITED) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }

    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (1 - 4 * 0.1 = 0.6 sec)
// 0
// (1 sec)
// 2
// (1 sec)
// 4
// (1 sec)
// 6
// (1 sec)
// 8
// (1 sec)
//sampleEnd
```


```
//5
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = produce(capacity = 3) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }

    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (1 - 2 * 0.1 = 0.8 sec)
// 0
// Sent
// (1 sec)
// 2
// Sent
// (1 sec)
// 4
// (1 sec)
// 6
// (1 sec)
// 8
// (1 sec)
//sampleEnd
```


```
//6
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = produce {
        // or produce(capacity = Channel.RENDEZVOUS) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }

    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

// 0
// Sent
// (1 sec)
// 2
// Sent
// (1 sec)
// 4
// Sent
// (1 sec)
// 6
// Sent
// (1 sec)
// 8
// Sent
// (1 sec)
//sampleEnd
```


```
//7
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = produce(capacity = Channel.CONFLATED) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }

    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (1 - 4 * 0.1 = 0.6 sec)
// 8
//sampleEnd
```


```
//8
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>(
        capacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    launch {
        repeat(5) { index ->
            channel.send(index * 2)
            delay(100)
            println("Sent")
        }
        channel.close()
    }

    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (0.1 sec)
// Sent
// (1 - 4 * 0.1 = 0.6 sec)
// 6
// (1 sec)
// 8
//sampleEnd
```


```
val channel = Channel<Resource>(capacity) { resource ->
    resource.close()
}
// or
// val channel = Channel<Resource>(
//      capacity,
//      onUndeliveredElement = { resource ->
//          resource.close()
//      }
// )

// Producer code
val resourceToSend = openResource()
channel.send(resourceToSend)

// Consumer code
val resourceReceived = channel.receive()
try {
    // work with received resource
} finally {
    resourceReceived.close()
}
```


```
//9
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

//sampleStart
fun CoroutineScope.produceNumbers() = produce {
    repeat(10) {
        delay(100)
        send(it)
    }
}

fun CoroutineScope.launchProcessor(
    id: Int,
    channel: ReceiveChannel<Int>
) = launch {
    for (msg in channel) {
        println("#$id received $msg")
    }
}

suspend fun main(): Unit = coroutineScope {
    val channel = produceNumbers()
    repeat(3) { id ->
        delay(10)
        launchProcessor(id, channel)
    }
}

// #0 received 0
// #1 received 1
// #2 received 2
// #0 received 3
// #1 received 4
// #2 received 5
// #0 received 6
// ...
//sampleEnd
```


```
//10
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

//sampleStart
suspend fun sendString(
    channel: SendChannel<String>,
    text: String,
    time: Long
) {
    while (true) {
        delay(time)
        channel.send(text)
    }
}

fun main() = runBlocking {
    val channel = Channel<String>()
    launch { sendString(channel, "foo", 200L) }
    launch { sendString(channel, "BAR!", 500L) }
    repeat(50) {
        println(channel.receive())
    }
    coroutineContext.cancelChildren()
}
// (200 ms)
// foo
// (200 ms)
// foo
// (100 ms)
// BAR!
// (100 ms)
// foo
// (200 ms)
// ...
//sampleEnd
```


```
fun <T> CoroutineScope.fanIn(
   channels: List<ReceiveChannel<T>>
): ReceiveChannel<T> = produce {
   for (channel in channels) {
       launch {
           for (elem in channel) {
               send(elem)
           }
       }
   }
}
```


```
//11
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

//sampleStart
// A channel of number from 1 to 3
fun CoroutineScope.numbers(): ReceiveChannel<Int> =
    produce {
        repeat(3) { num ->
            send(num + 1)
        }
    }

fun CoroutineScope.square(numbers: ReceiveChannel<Int>) =
    produce {
        for (num in numbers) {
            send(num * num)
        }
    }

suspend fun main() = coroutineScope {
    val numbers = numbers()
    val squared = square(numbers)
    for (num in squared) {
        println(num)
    }
}
// 1
// 4
// 9
//sampleEnd
```


```
suspend fun CoroutineScope.serveOrders(
    orders: ReceiveChannel<Order>,
    baristaName: String
): ReceiveChannel<CoffeeResult> = produce {
    for (order in orders) {
        val coffee = prepareCoffee(order.type)
        send(
            CoffeeResult(
                coffee = coffee,
                customer = order.customer,
                baristaName = baristaName
            )
        )
    }
}
```


```
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

data class Order(val customer: String, val type: CoffeeType)
enum class CoffeeType { ESPRESSO, LATTE }
class Milk
class GroundCoffee

sealed class Coffee

class Espresso(val ground: GroundCoffee) : Coffee() {
    override fun toString(): String = "Espresso"
}

class Latte(val milk: Milk, val espresso: Espresso) : Coffee() {
    override fun toString(): String = "Latte"
}

//sampleStart
suspend fun main() = coroutineScope<Unit> {
    val orders = List(100) { Order("Customer$it", CoffeeType.values().random()) }
    val ordersChannel = produce {
        orders.forEach { send(it) }
    }

    val coffeeResults = fanIn(
        serveOrders(ordersChannel, "Alex"),
        serveOrders(ordersChannel, "Bob"),
        serveOrders(ordersChannel, "Celine"),
    )

    for (coffeeResult in coffeeResults) {
        println("Serving $coffeeResult")
    }
}
//sampleEnd

fun <T> CoroutineScope.fanIn(
    vararg channels: ReceiveChannel<T>
): ReceiveChannel<T> = produce {
    for (channel in channels) {
        launch {
            for (elem in channel) {
                send(elem)
            }
        }
    }
}

data class CoffeeResult(val coffee: Coffee, val customer: String, val baristaName: String)

fun CoroutineScope.serveOrders(
    orders: ReceiveChannel<Order>,
    baristaName: String
): ReceiveChannel<CoffeeResult> = produce {
    for (order in orders) {
        val coffee = prepareCoffee(order.type)
        send(CoffeeResult(coffee, order.customer, baristaName))
    }
}

private fun prepareCoffee(type: CoffeeType): Coffee {
    val groundCoffee = groundCoffee()
    val espresso = makeEspresso(groundCoffee)
    val coffee = when (type) {
        CoffeeType.ESPRESSO -> espresso
        CoffeeType.LATTE -> {
            val milk = brewMilk()
            Latte(milk, espresso)
        }
    }
    return coffee
}

fun groundCoffee(): GroundCoffee {
    longOperation()
    return GroundCoffee()
}

fun brewMilk(): Milk {
    longOperation()
    return Milk()
}


fun makeEspresso(ground: GroundCoffee): Espresso {
    longOperation()
    return Espresso(ground)
}

fun longOperation() {
    //    val size = 820 // ~1 second on my MacBook
    val size = 350 // ~0.1 second on my MacBook
    val list = List(size) { it }
    val listOfLists = List(size) { list }
    val listOfListsOfLists = List(size) { listOfLists }
    listOfListsOfLists.hashCode()
}
```


```
// A simplified implementation
suspend fun handleOfferUpdates() = coroutineScope {
   val sellerChannel = listenOnSellerChanges()

   val offerToUpdateChannel = produce(capacity = UNLIMITED) {
       repeat(NUMBER_OF_CONCURRENT_OFFER_SERVICE_REQUESTS) {
           launch {
               for (seller in sellerChannel) {
                   val offers = offerService
                       .requestOffers(seller.id)
                   offers.forEach { send(it) }
               }
           }
       }
   }

   repeat(NUMBER_OF_CONCURRENT_UPDATE_SENDERS) {
       launch {
           for (offer in offerToUpdateChannel) {
               sendOfferUpdate(offer)
           }
       }
   }
}
```