package f_210_testing.s_11

@Test
fun `should load data concurrently`() = runTest {
    // given
    val userRepo = mockk<UserDataRepository>()
    coEvery { userRepo.getName() } coAnswers {
        delay(600)
        aName
    }
    coEvery { userRepo.getFriends() } coAnswers {
        delay(700)
        someFriends
    }
    coEvery { userRepo.getProfile() } coAnswers {
        delay(800)
        aProfile
    }
    val useCase = FetchUserUseCase(userRepo)

    // when
    useCase.fetchUserData()

    // then
    assertEquals(800, currentTime)
}
