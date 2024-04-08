package f_210_testing.s_14

@Test
fun `should show progress bar when sending data`() = runTest {
    val database = FakeDatabase()
    val vm = UserViewModel(database)
    launch {
        vm.showUserData()
    }

    // then
    assertEquals(false, vm.progressBarVisible.value)
    delay(1000)
    assertEquals(true, vm.progressBarVisible.value)
    delay(1000)
    assertEquals(false, vm.progressBarVisible.value)
}
