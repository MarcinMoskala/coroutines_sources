package f_301_channel.s_12

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
