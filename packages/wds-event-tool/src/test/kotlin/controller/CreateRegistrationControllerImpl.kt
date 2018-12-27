package controller

import arrow.core.Left
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import io.kotlintest.Description
import io.kotlintest.assertSoftly
import io.kotlintest.assertions.arrow.either.beRight
import io.kotlintest.assertions.arrow.either.beLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.spyk
import meta.enums.EventStatus
import meta.enums.ParticipantStatus
import meta.tables.pojos.Event
import meta.tables.pojos.Participant
import org.webdevandsausages.events.controllers.CreateRegistrationControllerImpl
import org.webdevandsausages.events.controllers.EventError
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import java.sql.Timestamp
import java.time.LocalDateTime

class CreateRegistrationControllerImplImplTest : StringSpec() {
    lateinit var unit: CreateRegistrationControllerImpl

    override fun beforeTest(description: Description) {
        unit = CreateRegistrationControllerImpl(
           emailService = mockk(relaxed = true),
           eventService = mockk(relaxed = true),
           randomTokenService = mockk(relaxed = true),
           logger = mockk(relaxed = true),
           registrationService = mockk(relaxed = true)
        )
    }

    private val newRegistration = RegistrationInDto(
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
            Timestamp.valueOf(LocalDateTime.now().plusDays(2))
            )
      )

    private val dbRegistration = ParticipantDto(
        email = "joe.schmo@mail.com",
        name = "Joe Schmo",
        orderNumber = 1000,
        status = ParticipantStatus.REGISTERED,
        verificationToken = "silly-token"
   )

    init {
        "happy case registration with REGISTERED status" {
            every { unit.eventService.getByIdOrLatest(any()) } returns Option(dbEvent)
            every { unit.randomTokenService.getWordPair() } returns "silly-token"
            val slot = slot<RegistrationInDto>()
            every { unit.registrationService.create(capture(slot)) } returns Option(dbRegistration)
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
                            orderNumber = 1000
                            )
                         )
                    )
               )
                slot.captured.shouldBe(
                    RegistrationInDto(
                        eventId=null,
                        firstName="Joe",
                        lastName="Schmo",
                        affiliation="Acme",
                        email="joe.schmo@mail.com",
                        registrationToken="silly-token",
                        orderNumber=1000,
                        status=ParticipantStatus.REGISTERED
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
                    Timestamp.valueOf(LocalDateTime.now().plusDays(-2))
                 ),
                 participants = listOf(
                    Participant(
                        1,
                        "Bill",
                        "Knox",
                        "billy@mail.com",
                        "Acme",
                        "silly-token",
                        1000,
                        1,
                        ParticipantStatus.REGISTERED)
                 )
            )
            every { unit.eventService.getByIdOrLatest(any()) } returns Option(fullEvent)
            every { unit.randomTokenService.getWordPair() } returns "silly-token"
            val slot2 = slot<RegistrationInDto>()
            val registration = dbRegistration.copy(status = ParticipantStatus.WAIT_LISTED)
            every { unit.registrationService.create(capture(slot2)) } returns Option(registration)
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
                            orderNumber = 1000
                        ))))
                slot2.captured.shouldBe(
                    RegistrationInDto(
                        eventId=null,
                        firstName="Joe",
                        lastName="Schmo",
                        affiliation="Acme",
                        email="joe.schmo@mail.com",
                        registrationToken="silly-token",
                        orderNumber=1000,
                        status=ParticipantStatus.WAIT_LISTED
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
                        ParticipantStatus.REGISTERED
                               )
                             )
                        )
            every { unit.eventService.getByIdOrLatest(any()) } returns Option(eventWithExisting)
            every { unit.randomTokenService.getWordPair() } returns "twofer-token"
            val spy = spyk(unit.registrationService)
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
                    Timestamp.valueOf(LocalDateTime.now().plusDays(-2))
                 )
              )
            every { unit.eventService.getByIdOrLatest(any()) } returns Option(dbEvent)
            every { unit.randomTokenService.getWordPair() } returns "twofer-token"
            val spy = spyk(unit.registrationService)
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
            every { unit.eventService.getByIdOrLatest(any()) } returns None
            every { unit.randomTokenService.getWordPair() } returns "twofer-token"
            val spy = spyk(unit.registrationService)
            every { spy.create(any()) } returns Option(dbRegistration)
            val resultingEither = unit(newRegistration)
            assertSoftly {
                resultingEither.isLeft().shouldBe(true)
                beLeft(resultingEither.shouldBe(Left(EventError.NotFound)))
                verify { spy wasNot Called }
            }
        }
    }
}