package f_310_testing_flow.s_3

class ObserveAppointmentsServiceTest {
    // ...
    
    @Test
    fun `should retry when API exception...`() = runTest {
        // given
        val repo = FakeAppointmentRepository(flow {
            emit(AppointmentsUpdate(listOf(anAppointment1)))
            throw ApiException(502, "Some message")
        })
        val service = ObserveAppointmentsService(repo)
        
        // when
        val result = service.observeAppointments()
            .take(3)
            .toList()
        
        // then
        assertEquals(
            listOf(
                listOf(anAppointment1),
                listOf(anAppointment1),
                listOf(anAppointment1),
            ), result
        )
    }
}
