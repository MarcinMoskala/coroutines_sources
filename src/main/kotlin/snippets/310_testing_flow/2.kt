package f_310_testing_flow.s_2

class ObserveAppointmentsServiceTest {
    // ...
    
    @Test
    fun `should eliminate elements that are...`() = runTest {
        // given
        val repo = FakeAppointmentRepository(flow {
            delay(1000)
            emit(AppointmentsUpdate(listOf(anAppointment1)))
            emit(AppointmentsUpdate(listOf(anAppointment1)))
            delay(1000)
            emit(AppointmentsUpdate(listOf(anAppointment2)))
            delay(1000)
            emit(AppointmentsUpdate(listOf(anAppointment2)))
            emit(AppointmentsUpdate(listOf(anAppointment1)))
        })
        val service = ObserveAppointmentsService(repo)
        
        // when
        val result = service.observeAppointments()
            .map { currentTime to it }
            .toList()
        
        // then
        assertEquals(
            listOf(
                1000L to listOf(anAppointment1),
                2000L to listOf(anAppointment2),
                3000L to listOf(anAppointment1),
            ), result
        )
    }
    
    // ...
}
