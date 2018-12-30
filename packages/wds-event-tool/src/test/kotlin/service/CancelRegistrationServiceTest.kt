package service

import arrow.core.Option
import io.kotlintest.Description
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import meta.enums.EventStatus
import meta.enums.ParticipantStatus
import meta.tables.pojos.Event
import meta.tables.pojos.Participant
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.service.CancelRegistrationService
import java.sql.Timestamp
import java.time.LocalDateTime

class CancelRegistrationServiceTest : StringSpec() {
    lateinit var unit: CancelRegistrationService

    override fun beforeTest(description: Description) {
        unit = CancelRegistrationService(eventCRUD = mockk(relaxed = true), participantCRUD = mockk(relaxed = false))
    }

    private val TIMESTAMP = Timestamp.valueOf(LocalDateTime.now().minusDays(4))

    private var dbEvent = EventDto(
        event = Event(
            1,
            "Vol 10",
            "Acme",
            "Joe",
            Timestamp.valueOf(LocalDateTime.now().plusDays(4)),
            "Great presentations",
            "Tampere center",
            EventStatus.OPEN,
            80,
            Timestamp.valueOf(LocalDateTime.now().plusDays(2)),
            TIMESTAMP,
            TIMESTAMP
        )
    )

    private val updatedParticipant = Participant(
        1L,
        "Joe",
        "Schmo",
        "joe.schmo@mail.com",
        "Google",
        "silly-token",
        1000,
        1L,
        ParticipantStatus.REGISTERED,
        TIMESTAMP,
        TIMESTAMP
    )

    init {
        "Participant cancels registration when there is no waiting list" {
            every { unit.eventCRUD.findByParticipantToken("silly-token") } returns Option(dbEvent)
            val slot = slot<Long>()
            val slot2 = slot<ParticipantStatus>()
            every { unit.participantCRUD.updateStatus(capture(slot), capture(slot2)) } returns Option(updatedParticipant)
            val resultingEither = unit("silly-token")
            /* assertSoftly {
                resultingEither.shouldBeRight()
                beRight(
                    resultingEither.shouldBe(
                        Right(
                            ParticipantDto(
                                email = "joe.schmo@mail.com",
                                name = "Joe Schmo",
                                verificationToken = "silly-token",
                                status = ParticipantStatus.REGISTERED,
                                orderNumber = 1,
                                insertedOn = TIMESTAMP.prettified
                            )
                        )
                    )
                )
                slot.captured.shouldBe(
                    RegistrationInDto(
                        eventId = null,
                        firstName = "Joe",
                        lastName = "Schmo",
                        affiliation = "Acme",
                        email = "joe.schmo@mail.com",
                        registrationToken = "silly-token",
                        orderNumber = 1000,
                        status = ParticipantStatus.REGISTERED
                    )
                )
            } */
        }

    }
}