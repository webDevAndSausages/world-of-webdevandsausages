package service

import io.kotlintest.Description
import io.kotlintest.specs.StringSpec
import org.webdevandsausages.events.service.CancelRegistrationService
import java.sql.Timestamp
import java.time.LocalDateTime

class CancelRegistrationServiceTest : StringSpec() {
    lateinit var unit: CancelRegistrationService

    override fun beforeTest(description: Description) {
        unit = CancelRegistrationService()
    }

    private val TIMESTAMP = Timestamp.valueOf(LocalDateTime.now().minusDays(4))

    // TODO: Implement service tests

    /* private val newRegistration = RegistrationInDto(
        email = "joe.schmo@mail.com",
        firstName = "Joe",
        lastName = "Schmo",
        affiliation = "Acme"
    )

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

    private val dbRegistration = ParticipantDto(
        email = "joe.schmo@mail.com",
        name = "Joe Schmo",
        orderNumber = 1000,
        status = ParticipantStatus.REGISTERED,
        verificationToken = "silly-token",
        insertedOn = TIMESTAMP.prettified
        )

    init {
        "happy case registration with REGISTERED status" {
            every { unit.eventRepository.findByIdOrLatest(any()) } returns Option(dbEvent)
            every { unit.randomWordsUtil.getWordPair() } returns "silly-token"
            val slot = slot<RegistrationInDto>()
            every { unit.participantRepository.create(capture(slot)) } returns Option(dbRegistration)
            val resultingEither = unit(newRegistration)
            assertSoftly {
                resultingEither.shouldBeRight()
                beRight(
                    resultingEither.shouldBe(
                        Right(ParticipantDto(
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
            }
        }

        "should register with WAITING_LIST status if maxParticipants is reached" {
            var fullEvent = EventDto(
                event = Event(
                    1,
                    "Vol 10",
                    "Acme",
                    "Joe",
                    Timestamp.valueOf(LocalDateTime.now().plusDays(4)),
                    "Great presentations",
                    "Tampere center",
                    EventStatus.OPEN_WITH_WAITLIST,
                    1,
                    Timestamp.valueOf(LocalDateTime.now().plusDays(-2)),
                    TIMESTAMP,
                    TIMESTAMP
                    ),
                participants = listOf(
                     Participant(
                        1,
                        "Bill",
                        "Knox",
                        "billy@mail.com",
                        "Acme",
                        "silly-token",
                        2000,
                        1,
                        ParticipantStatus.REGISTERED,
                        TIMESTAMP,
                        TIMESTAMP
                        ))
            )
            every { unit.eventRepository.findByIdOrLatest(any()) } returns Option(fullEvent)
            every { unit.randomWordsUtil.getWordPair() } returns "silly-token"
            val slot2 = slot<RegistrationInDto>()
            val registration = dbRegistration.copy(status = ParticipantStatus.WAIT_LISTED)
            every { unit.participantRepository.create(capture(slot2)) } returns Option(registration)
            val resultingEither = unit(newRegistration)
            assertSoftly {
                resultingEither.shouldBeRight()
                beRight(
                    resultingEither.shouldBe(
                        Right(ParticipantDto(
                            email = "joe.schmo@mail.com",
                            name = "Joe Schmo",
                            verificationToken = "silly-token",
                            status = ParticipantStatus.WAIT_LISTED,
                            orderNumber = 1,
                            insertedOn = TIMESTAMP.prettified
                            ))))
                slot2.captured.shouldBe(
                    RegistrationInDto(
                        eventId = null,
                        firstName = "Joe",
                        lastName = "Schmo",
                        affiliation = "Acme",
                        email = "joe.schmo@mail.com",
                        registrationToken = "silly-token",
                        orderNumber = 3000,
                        status = ParticipantStatus.WAIT_LISTED
                        )
                    )
            }
        }

        "should return error if email is already among participants" {
            val eventWithExisting = dbEvent.copy(
                participants = listOf(
                    Participant(
                        1,
                        "Joe",
                        "Schmo",
                        "joe.schmo@mail.com",
                        "Acme",
                        "silly-token",
                        1000,
                        1,
                        ParticipantStatus.REGISTERED,
                        TIMESTAMP,
                        TIMESTAMP
                        )
                    )
                )
            every { unit.eventRepository.findByIdOrLatest(any()) } returns Option(eventWithExisting)
            every { unit.randomWordsUtil.getWordPair() } returns "twofer-token"
            val spy = spyk(unit.participantRepository)
            every { spy.create(any()) } returns Option(dbRegistration)
            val resultingEither = unit(newRegistration)
            assertSoftly {
                resultingEither.isLeft().shouldBe(true)
                beLeft(resultingEither.shouldBe(Left(EventError.AlreadyRegistered)))
                verify { spy wasNot Called }
            }
        }

        "should return NotFound error if event is closed" {
            var dbEvent = EventDto(
                event = Event(
                    1,
                    "Vol 10",
                    "Acme",
                    "Joe",
                    Timestamp.valueOf(LocalDateTime.now().plusDays(0)),
                    "Great presentations",
                    "Tampere center",
                    EventStatus.CLOSED,
                    80,
                    Timestamp.valueOf(LocalDateTime.now().plusDays(-2)),
                    TIMESTAMP,
                    TIMESTAMP
                    )
                )
            every { unit.eventRepository.findByIdOrLatest(any()) } returns Option(dbEvent)
            every { unit.randomWordsUtil.getWordPair() } returns "twofer-token"
            val spy = spyk(unit.participantRepository)
            every { spy.create(any()) } returns Option(dbRegistration)
            val resultingEither = unit(newRegistration)
            assertSoftly {
                resultingEither.isLeft().shouldBe(true)
                beLeft(resultingEither.shouldBe(Left(EventError.NotFound))
                    )
                verify { spy wasNot Called }
            }
        }

        "should return NotFound if event does not exist" {
            every { unit.eventRepository.findByIdOrLatest(any()) } returns None
            every { unit.randomWordsUtil.getWordPair() } returns "twofer-token"
            val spy = spyk(unit.participantRepository)
            every { spy.create(any()) } returns Option(dbRegistration)
            val resultingEither = unit(newRegistration)
            assertSoftly {
                resultingEither.isLeft().shouldBe(true)
                beLeft(resultingEither.shouldBe(Left(EventError.NotFound)))
                verify { spy wasNot Called }
            }
        }
    } */
}