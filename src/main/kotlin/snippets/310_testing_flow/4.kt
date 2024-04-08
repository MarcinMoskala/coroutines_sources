package f_310_testing_flow.s_4

class ObserveAppointmentsServiceTest {
    // ...
    
    @Test
    fun `should retry when API exception...`() = runTest {
        // given
        var retried = false
        val someException = object : Exception() {}
        val repo = FakeAppointmentRepository(flow {
            emit(AppointmentsUpdate(listOf(anAppointment1)))
            if (!retried) {
                retried = true
                throw ApiException(502, "Some message")
            } else {
                throw someException
            }
        })
        val service = ObserveAppointmentsService(repo)
        
        // when
        val result = service.observeAppointments()
            .catch<Any> { emit(it) }
            .toList()
        
        // then
        assertTrue(retried)
        assertEquals(
            listOf(
                listOf(anAppointment1),
                listOf(anAppointment1),
                someException,
            ), result
        )
    }
}
