package f_210_testing.s_1

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class FetchUserUseCase(
    private val repo: UserDataRepository,
) {

    suspend fun fetchUserData(): User = coroutineScope {
        val name = async { repo.getName() }
        val friends = async { repo.getFriends() }
        val profile = async { repo.getProfile() }
        User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await()
        )
    }
}

class FetchUserDataTest {

    @Test
    fun `should construct user`() = runBlocking {
        // given
        val repo = FakeUserDataRepository()
        val useCase = FetchUserUseCase(repo)

        // when
        val result = useCase.fetchUserData()

        // then
        val expectedUser = User(
            name = "Ben",
            friends = listOf(Friend("some-friend-id-1")),
            profile = Profile("Example description")
        )
        assertEquals(expectedUser, result)
    }

    class FakeUserDataRepository : UserDataRepository {
        override suspend fun getName(): String = "Ben"

        override suspend fun getFriends(): List<Friend> =
            listOf(Friend("some-friend-id-1"))

        override suspend fun getProfile(): Profile =
            Profile("Example description")
    }
}


interface UserDataRepository {
    suspend fun getName(): String
    suspend fun getFriends(): List<Friend>
    suspend fun getProfile(): Profile
}

data class User(
    val name: String,
    val friends: List<Friend>,
    val profile: Profile
)

data class Friend(val id: String)
data class Profile(val description: String)
