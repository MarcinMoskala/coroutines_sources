```
class NotificationsSender(
    private val client: NotificationsClient,
    private val notificationScope: CoroutineScope,
) {
    fun sendNotifications(
        notifications: List<Notification>
    ) {
        for (n in notifications) {
            notificationScope.launch {
                client.send(n)
            }
        }
    }
}
```


```
class NotificationsSender(
    private val client: NotificationsClient,
) {
    suspend fun sendNotifications(
        notifications: List<Notification>
    ) = supervisorScope {
        for (n in notifications) {
            launch {
                client.send(n)
            }
        }
    }
}
```


```
class NotificationsSender(
    private val client: NotificationsClient,
    private val notificationScope: CoroutineScope,
) {
    // Does not wait for started coroutines
    // Exceptions are handled by the scope
    // Takes context from the scope
    // and builds relationship to the scope
    fun sendNotifications(
        notifications: List<Notification>
    ) {
        // ...
    }
}
```


```
class NotificationsSender(
    private val client: NotificationsClient,
) {
    // Waits for its coroutines
    // Handles exceptions
    // Takes context and builds relationship to
    // the coroutine that started it
    suspend fun sendNotifications(
        notifications: List<Notification>
    ) {
        // ...
    }
}
```


```
suspend fun updateUser() = coroutineScope {
    val apiUserAsync = async { api.fetchUser() }
    val dbUserAsync = async { db.getUser() }
    val apiUser = apiUserAsync.await()
    val dbUser = dbUserAsync.await()
    
    if (apiUser.lastUpdate > dbUser.lastUpdate) {
        db.updateUser(apiUser)
    } {
        api.updateUser(dbUser)
    }
    
    eventsScope.launch { sendEvent(UserSunchronized) }
}
```