package f_210_testing.s_13

@Test
fun `should show progress bar when sending data`() = runTest {
    // given
    val database = FakeDatabase()
    val vm = UserViewModel(database)

    // when
    launch {
        vm.sendUserData()
    }

    // then
    assertEquals(false, vm.progressBarVisible.value)

    // when
    advanceTimeBy(1000)

    // then
    assertEquals(false, vm.progressBarVisible.value)

    // when
    runCurrent()

    // then
    assertEquals(true, vm.progressBarVisible.value)

    // when
    advanceUntilIdle()

    // then
    assertEquals(false, vm.progressBarVisible.value)
}
