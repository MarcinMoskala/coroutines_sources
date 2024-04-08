package f_310_testing_flow.s_1

class FakeAppointmentRepository(
    private val flow: Flow<AppointmentsEvent>
) : AppointmentRepository {
    override fun observeAppointments() = flow
}

class ObserveAppointmentsServiceTest {
    val aDate1 = Instant.parse("2020-08-30T18:43:00Z")
    val anAppointment1 = Appointment("APP1", aDate1)
    val aDate2 = Instant.parse("2020-08-31T18:43:00Z")
    val anAppointment2 = Appointment("APP2", aDate2)
    
    @Test
    fun `should keep only appointments from...`() = runTest {
        // given
        val repo = FakeAppointmentRepository(
            flowOf(
                AppointmentsConfirmed,
                AppointmentsUpdate(listOf(anAppointment1)),
                AppointmentsUpdate(listOf(anAppointment2)),
                AppointmentsConfirmed,
            )
        )
        val service = ObserveAppointmentsService(repo)
        
        // when
        val result = service.observeAppointments().toList()
        
        // then
        assertEquals(
            listOf(
                listOf(anAppointment1),
                listOf(anAppointment2),
            ),
            result
        )
    }
    
    // ...
}
