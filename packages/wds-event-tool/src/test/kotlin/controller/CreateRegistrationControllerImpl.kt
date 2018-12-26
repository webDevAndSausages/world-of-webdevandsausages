package controller

import arrow.core.Left
import arrow.core.Option
import arrow.core.Right
import io.kotlintest.Description
import io.kotlintest.assertSoftly
import io.kotlintest.assertions.arrow.either.beRight
import io.kotlintest.assertions.arrow.either.beLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
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
        "registration happy case" {
            every { unit.eventService.getByIdOrLatest(any()) } returns Option(dbEvent)
            every { unit.randomTokenService.getWordPair() } returns "silly-token"
            every { unit.registrationService.create(any()) } returns Option(dbRegistration)
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
            every { unit.registrationService.create(any()) } returns Option(dbRegistration)
            val resultingEither = unit(newRegistration)
            assertSoftly {
                resultingEither.isLeft().shouldBe(true)
                beLeft(resultingEither.shouldBe(Left(EventError.AlreadyRegistered)))
            }
        }

        "should error if event is closed is already among participants" {
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
            every { unit.registrationService.create(any()) } returns Option(dbRegistration)
            val resultingEither = unit(newRegistration)
            assertSoftly {
                resultingEither.isLeft().shouldBe(true)
                beLeft(resultingEither.shouldBe(Left(EventError.NotFound))
                  )
            }
        }
    }
}