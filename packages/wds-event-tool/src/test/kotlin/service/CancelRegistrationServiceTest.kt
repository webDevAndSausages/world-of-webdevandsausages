package service

import arrow.core.Option
import arrow.core.Right
import io.kotlintest.Description
import io.kotlintest.assertSoftly
import io.kotlintest.assertions.arrow.either.beRight
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import meta.enums.EventStatus
import meta.enums.ParticipantStatus
import meta.tables.pojos.Event
import meta.tables.pojos.Participant
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.service.CancelRegistrationService
import org.webdevandsausages.events.utils.prettified
import java.sql.Timestamp
import java.time.LocalDateTime

class CancelRegistrationServiceTest : StringSpec() {
    lateinit var unit: CancelRegistrationService

    override fun beforeTest(description: Description) {
        unit = CancelRegistrationService(eventCRUD = mockk(relaxed = true), participantCRUD = mockk(relaxed = false))
    }

    private val TIMESTAMP = Timestamp.valueOf(LocalDateTime.now().minusDays(4))
    private val testEvent = Event(
        1,
        "Vol 10",
        "Acme",
        "Joe",
        Timestamp.valueOf(LocalDateTime.now().plusDays(4)),
        "Great presentations",
        "Tampere center",
        EventStatus.OPEN,
        3,
        Timestamp.valueOf(LocalDateTime.now().plusDays(2)),
        TIMESTAMP,
        TIMESTAMP
    )

    private val dbEvent = EventDto(
        event = testEvent
    )

    private val cancelledParticipant = Participant(
        1L,
        "Joe",
        "Schmo",
        "joe.schmo@mail.com",
        "Google",
        "silly-token",
        1000,
        1L,
        ParticipantStatus.CANCELLED,
        TIMESTAMP,
        TIMESTAMP
    )

    init {
        "Participant cancels registration when there is no waiting list" {
            dbEvent.participants = listOf(
                Participant(
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
                ),
                Participant(
                    2L,
                    "Ann",
                    "Bann",
                    "ann.bann@mail.com",
                    "Yahoo",
                    "fishy-token",
                    2000,
                    1L,
                    ParticipantStatus.REGISTERED,
                    TIMESTAMP,
                    TIMESTAMP
                )
            )
            every { unit.eventCRUD.findByParticipantToken("silly-token") } returns Option(dbEvent)
            val slot = slot<ParticipantStatus>()
            every { unit.participantCRUD.updateStatus(1L, capture(slot)) } returns Option(cancelledParticipant)
            val resultingEither = unit("silly-token")
            assertSoftly {
                resultingEither.shouldBeRight()
                beRight(
                    resultingEither.shouldBe(
                        Right(
                            ParticipantDto(
                                email = "joe.schmo@mail.com",
                                name = "Joe Schmo",
                                verificationToken = "silly-token",
                                affiliation = "Google",
                                status = ParticipantStatus.CANCELLED,
                                orderNumber = 1000,
                                insertedOn = TIMESTAMP.prettified
                            )
                        )
                    )
                )
                slot.captured.shouldBe(ParticipantStatus.CANCELLED)
            }
        }
    }
}