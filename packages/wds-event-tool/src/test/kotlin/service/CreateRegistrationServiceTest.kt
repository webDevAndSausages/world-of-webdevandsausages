package service

import CreateRegistrationService
import arrow.core.Left
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import io.kotlintest.Description
import io.kotlintest.assertSoftly
import io.kotlintest.assertions.arrow.either.beLeft
import io.kotlintest.assertions.arrow.either.beRight
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.*
import meta.enums.EventStatus
import meta.enums.ParticipantStatus
import meta.tables.pojos.Event
import meta.tables.pojos.Participant
import org.jooq.Configuration
import org.jooq.impl.DSL
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.domain.EventError
import org.webdevandsausages.events.utils.prettified
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.OffsetDateTime

class CreateRegistrationServiceTest : StringSpec() {
    lateinit var unit: CreateRegistrationService

    override fun beforeTest(description: Description) {
        unit = CreateRegistrationService(
               emailService = mockk(relaxed = true),
               eventCRUD = mockk(relaxed = true),
               randomWordsUtil = mockk(relaxed = true),
               participantCRUD = mockk(relaxed = true)
               )
        every { unit.eventCRUD.db } returns DSL.using(mockkClass(Configuration::class, relaxed = true))
        every { unit.participantCRUD.db } returns DSL.using(mockkClass(Configuration::class, relaxed = true))
    }

    private val TIMESTAMP = Timestamp.valueOf(LocalDateTime.now().minusDays(4))

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
            OffsetDateTime.now().plusDays(4),
            "Great presentations",
            "Tampere center",
            EventStatus.OPEN,
            80,
            Timestamp.valueOf(LocalDateTime.now().plusDays(2)),
            TIMESTAMP,
            TIMESTAMP,
            10,
            "www.acme.fi"
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
            every { unit.eventCRUD.findByIdOrLatest(any()) } returns Option(dbEvent)
            every { unit.randomWordsUtil.getWordPair() } returns "silly-token"
            val slot = slot<RegistrationInDto>()
            every { unit.participantCRUD.create(capture(slot)) } returns Option(dbRegistration)
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
                    OffsetDateTime.now().plusDays(4),
                    "Great presentations",
                    "Tampere center",
                    EventStatus.OPEN_WITH_WAITLIST,
                    1,
                    Timestamp.valueOf(LocalDateTime.now().plusDays(-2)),
                    TIMESTAMP,
                    TIMESTAMP,
                    10,
                    "www.acme.fi"
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
            every { unit.eventCRUD.findByIdOrLatest(any()) } returns Option(fullEvent)
            every { unit.randomWordsUtil.getWordPair() } returns "silly-token"
            val slot2 = slot<RegistrationInDto>()
            val registration = dbRegistration.copy(status = ParticipantStatus.WAIT_LISTED)
            every { unit.participantCRUD.create(capture(slot2)) } returns Option(registration)
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
            every { unit.eventCRUD.findByIdOrLatest(any()) } returns Option(eventWithExisting)
            every { unit.randomWordsUtil.getWordPair() } returns "twofer-token"
            val spy = spyk(unit.participantCRUD)
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
                    OffsetDateTime.now().plusDays(0),
                    "Great presentations",
                    "Tampere center",
                    EventStatus.CLOSED,
                    80,
                    Timestamp.valueOf(LocalDateTime.now().plusDays(-2)),
                    TIMESTAMP,
                    TIMESTAMP,
                    10,
                    "www.acme.fi"
                    )
                )
            every { unit.eventCRUD.findByIdOrLatest(any()) } returns Option(dbEvent)
            every { unit.randomWordsUtil.getWordPair() } returns "twofer-token"
            val spy = spyk(unit.participantCRUD)
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
            every { unit.eventCRUD.findByIdOrLatest(any()) } returns None
            every { unit.randomWordsUtil.getWordPair() } returns "twofer-token"
            val spy = spyk(unit.participantCRUD)
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
