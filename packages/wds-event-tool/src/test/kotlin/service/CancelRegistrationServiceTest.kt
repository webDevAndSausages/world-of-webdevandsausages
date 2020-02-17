package service

import arrow.core.Left
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.toOption
import io.kotlintest.Description
import io.kotlintest.assertSoftly
import io.kotlintest.assertions.arrow.either.beLeft
import io.kotlintest.assertions.arrow.either.beRight
import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.slot
import meta.enums.EventStatus
import meta.enums.ParticipantStatus
import meta.tables.pojos.Event
import meta.tables.pojos.Participant
import org.jooq.Configuration
import org.jooq.impl.DSL
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.domain.RegistrationCancellationError
import org.webdevandsausages.events.service.registration.CancelRegistrationService
import org.webdevandsausages.events.utils.prettified
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.OffsetDateTime

class CancelRegistrationServiceTest : StringSpec() {
    lateinit var unit: CancelRegistrationService

    override fun beforeTest(description: Description) {
        unit = CancelRegistrationService(
            eventCRUD = mockk(relaxed = true),
            participantCRUD = mockk(relaxed = true),
            emailService = mockk(relaxed = true)
        )

        every { unit.participantCRUD.db } returns DSL.using(mockkClass(Configuration::class, relaxed = true))
        every { unit.eventCRUD.db } returns DSL.using(mockkClass(Configuration::class, relaxed = true))
    }

    private val TIMESTAMP = Timestamp.valueOf(LocalDateTime.now().minusDays(4))
    private val testEvent = Event(
        1,
        "Vol 10",
        "Acme",
        "Joe",
        OffsetDateTime.now().plusDays(4),
        "Great presentations",
        "Tampere center",
        EventStatus.OPEN,
        3,
        Timestamp.valueOf(LocalDateTime.now().plusDays(2)),
        TIMESTAMP,
        TIMESTAMP,
        10,
        "www.acme.fi"
    )

    private val dbEvent = EventDto(
        event = testEvent
    )

    init {
        "Participant cancels registration when there is no waiting list" {
            val cancelledParticipant = Participant(
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

        "Participant cancels registration while participant is on the wait list himself" {
            val cancelledParticipant = Participant(
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
                    ParticipantStatus.WAIT_LISTED,
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

        "Participant cancels registration while registered and there are people on the waiting list" {
            val cancelledParticipant1 = Participant(
                3L,
                "Willy",
                "Nilly",
                "willy.nilly@mail.com",
                "Kazaa",
                "cosher-token",
                3000,
                1L,
                ParticipantStatus.CANCELLED,
                TIMESTAMP,
                TIMESTAMP
            )

            val luckyParticipant1 = Participant(
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

            val luckyParticipant2 = Participant(
                4L,
                "John",
                "Schmon",
                "john.schmon@mail.com",
                "Apple",
                "strange-token",
                4000,
                1L,
                ParticipantStatus.REGISTERED,
                TIMESTAMP,
                TIMESTAMP
            )

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
                    ParticipantStatus.WAIT_LISTED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    3L,
                    "Willy",
                    "Nilly",
                    "willy.nilly@mail.com",
                    "Kazaa",
                    "cosher-token",
                    3000,
                    1L,
                    ParticipantStatus.REGISTERED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    4L,
                    "John",
                    "Schmon",
                    "john.schmon@mail.com",
                    "Apple",
                    "strange-token",
                    4000,
                    1L,
                    ParticipantStatus.WAIT_LISTED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    5L,
                    "Gillian",
                    "Schmillian",
                    "gillian.schmillian@mail.com",
                    "X-Filez",
                    "supernatural-token",
                    5000,
                    1L,
                    ParticipantStatus.WAIT_LISTED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    6L,
                    "Ford",
                    "Schmord",
                    "ford.schmord@mail.com",
                    "School of Ancient Arts",
                    "indiana-token",
                    6000,
                    1L,
                    ParticipantStatus.REGISTERED,
                    TIMESTAMP,
                    TIMESTAMP
                )
            )
            every { unit.eventCRUD.findByParticipantToken("cosher-token") } returns Option(dbEvent)
            val slot = slot<ParticipantStatus>()
            val slot2 = slot<ParticipantStatus>()
            val slot3 = slot<ParticipantDto>()
            val slot4 = slot<ParticipantDto>()
            every { unit.participantCRUD.updateStatus(3L, capture(slot)) } returns Option(cancelledParticipant1)
            every { unit.participantCRUD.updateStatus(2L, capture(slot2)) } returns Option(luckyParticipant1)
            every { unit.emailService.sendCancelConfirmationEmail(dbEvent, capture(slot3)) } just Runs
            every { unit.emailService.sendRegistrationEmailForWaitListed(dbEvent, capture(slot4)) } just Runs

            /**
             * First test case here:
             * Cancelling registration for Willy opens up a spot for Ann
             */
            val resultingEither = unit("cosher-token")
            assertSoftly {
                resultingEither.shouldBeRight()
                beRight(
                    resultingEither.shouldBe(
                        Right(
                            ParticipantDto(
                                email = "willy.nilly@mail.com",
                                name = "Willy Nilly",
                                verificationToken = "cosher-token",
                                affiliation = "Kazaa",
                                status = ParticipantStatus.CANCELLED,
                                orderNumber = 3000,
                                insertedOn = TIMESTAMP.prettified
                            )
                        )
                    )
                )
                slot.captured.shouldBe(ParticipantStatus.CANCELLED)
                // Participant ID 2 should be next in line
                slot2.captured.shouldBe(ParticipantStatus.REGISTERED)

                // cancel confirmation email should have been sent to user id 3
                slot3.captured.shouldBe(
                    ParticipantDto(
                        email = "willy.nilly@mail.com",
                        name = "Willy Nilly",
                        verificationToken = "cosher-token",
                        affiliation = "Kazaa",
                        status = ParticipantStatus.CANCELLED,
                        orderNumber = 3000,
                        insertedOn = TIMESTAMP.prettified
                    )
                )

                // Lucky participant who just got registered should get a confirmation email
                slot4.captured.shouldBe(
                    ParticipantDto(
                        email = "ann.bann@mail.com",
                        name = "Ann Bann",
                        verificationToken = "fishy-token",
                        affiliation = "Yahoo",
                        status = ParticipantStatus.REGISTERED,
                        orderNumber = 2000,
                        insertedOn = TIMESTAMP.prettified
                    )
                )
            }

            /**
             * Second test case here:
             * When just registered Ann decides to cancel it opens a spot for John
             */

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
                ),
                Participant(
                    3L,
                    "Willy",
                    "Nilly",
                    "willy.nilly@mail.com",
                    "Kazaa",
                    "cosher-token",
                    3000,
                    1L,
                    ParticipantStatus.CANCELLED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    4L,
                    "John",
                    "Schmon",
                    "john.schmon@mail.com",
                    "Apple",
                    "strange-token",
                    4000,
                    1L,
                    ParticipantStatus.WAIT_LISTED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    5L,
                    "Gillian",
                    "Schmillian",
                    "gillian.schmillian@mail.com",
                    "X-Filez",
                    "supernatural-token",
                    5000,
                    1L,
                    ParticipantStatus.WAIT_LISTED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    6L,
                    "Ford",
                    "Schmord",
                    "ford.schmord@mail.com",
                    "School of Ancient Arts",
                    "indiana-token",
                    6000,
                    1L,
                    ParticipantStatus.REGISTERED,
                    TIMESTAMP,
                    TIMESTAMP
                )
            )

            every { unit.eventCRUD.findByParticipantToken("fishy-token") } returns Option(dbEvent)
            every { unit.participantCRUD.updateStatus(2L, capture(slot)) } returns Option(object : Participant(
                luckyParticipant1
            ) {
                override fun getStatus(): ParticipantStatus {
                    return ParticipantStatus.CANCELLED
                }
            })
            every { unit.participantCRUD.updateStatus(4L, capture(slot2)) } returns Option(luckyParticipant2)
            every { unit.emailService.sendCancelConfirmationEmail(dbEvent, capture(slot3)) } just Runs
            every { unit.emailService.sendRegistrationEmailForWaitListed(dbEvent, capture(slot4)) } just Runs

            val resultingEither2 = unit("fishy-token")
            assertSoftly {
                resultingEither2.shouldBeRight()
                beRight(
                    resultingEither2.shouldBe(
                        Right(
                            ParticipantDto(
                                email = "ann.bann@mail.com",
                                name = "Ann Bann",
                                verificationToken = "fishy-token",
                                affiliation = "Yahoo",
                                status = ParticipantStatus.CANCELLED,
                                orderNumber = 2000,
                                insertedOn = TIMESTAMP.prettified
                            )
                        )
                    )
                )
                slot.captured.shouldBe(ParticipantStatus.CANCELLED)
                // Participant ID 4 should be next in line
                slot2.captured.shouldBe(ParticipantStatus.REGISTERED)

                // cancel confirmation email should have been sent to user id 2
                slot3.captured.shouldBe(
                    ParticipantDto(
                        email = "ann.bann@mail.com",
                        name = "Ann Bann",
                        verificationToken = "fishy-token",
                        affiliation = "Yahoo",
                        status = ParticipantStatus.CANCELLED,
                        orderNumber = 2000,
                        insertedOn = TIMESTAMP.prettified
                    )
                )

                // Second lucky participant who just got registered should get a confirmation email
                slot4.captured.shouldBe(
                    ParticipantDto(
                        email = "john.schmon@mail.com",
                        name = "John Schmon",
                        verificationToken = "strange-token",
                        affiliation = "Apple",
                        status = ParticipantStatus.REGISTERED,
                        orderNumber = 4000,
                        insertedOn = TIMESTAMP.prettified
                    )
                )
            }
        }

        "Participant who is a ORGANIZER cancels registration and it should not affect waiting list" {
            val cancelledParticipant1 = Participant(
                2L,
                "Ann",
                "Bann",
                "ann.bann@mail.com",
                "Yahoo",
                "fishy-token",
                2000,
                1L,
                ParticipantStatus.CANCELLED,
                TIMESTAMP,
                TIMESTAMP
            )

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
                    ParticipantStatus.ORGANIZER,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    3L,
                    "Willy",
                    "Nilly",
                    "willy.nilly@mail.com",
                    "Kazaa",
                    "cosher-token",
                    3000,
                    1L,
                    ParticipantStatus.REGISTERED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    4L,
                    "John",
                    "Schmon",
                    "john.schmon@mail.com",
                    "Apple",
                    "strange-token",
                    4000,
                    1L,
                    ParticipantStatus.WAIT_LISTED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    5L,
                    "Gillian",
                    "Schmillian",
                    "gillian.schmillian@mail.com",
                    "X-Filez",
                    "supernatural-token",
                    5000,
                    1L,
                    ParticipantStatus.WAIT_LISTED,
                    TIMESTAMP,
                    TIMESTAMP
                ),
                Participant(
                    6L,
                    "Ford",
                    "Schmord",
                    "ford.schmord@mail.com",
                    "School of Ancient Arts",
                    "indiana-token",
                    6000,
                    1L,
                    ParticipantStatus.REGISTERED,
                    TIMESTAMP,
                    TIMESTAMP
                )
            )
            every { unit.eventCRUD.findByParticipantToken("fishy-token") } returns Option(dbEvent)
            val slot = slot<ParticipantStatus>()
            val slot3 = slot<ParticipantDto>()
            val slot4 = slot<ParticipantDto>()
            every { unit.participantCRUD.updateStatus(any(), capture(slot)) } returns Option(cancelledParticipant1)
            every { unit.emailService.sendCancelConfirmationEmail(dbEvent, capture(slot3)) } just Runs
            every { unit.emailService.sendRegistrationEmailForWaitListed(dbEvent, capture(slot4)) } just Runs

            /**
             * Cancelling registration for organizer shouldn't affect waiting list
             */
            val resultingEither = unit("fishy-token")
            assertSoftly {
                resultingEither.shouldBeRight()
                beRight(
                    resultingEither.shouldBe(
                        Right(
                            ParticipantDto(
                                email = "ann.bann@mail.com",
                                name = "Ann Bann",
                                verificationToken = "fishy-token",
                                affiliation = "Yahoo",
                                status = ParticipantStatus.CANCELLED,
                                orderNumber = 2000,
                                insertedOn = TIMESTAMP.prettified
                            )
                        )
                    )
                )
                slot.captured.shouldBe(ParticipantStatus.CANCELLED)

                // cancel confirmation email should have been sent to user id 2
                slot3.captured.shouldBe(
                    ParticipantDto(
                        email = "ann.bann@mail.com",
                        name = "Ann Bann",
                        verificationToken = "fishy-token",
                        affiliation = "Yahoo",
                        status = ParticipantStatus.CANCELLED,
                        orderNumber = 2000,
                        insertedOn = TIMESTAMP.prettified
                    )
                )

                // No people from waiting list should have been moved to registered after this cancellation
                slot4.isCaptured.shouldBe(false)
            }
        }

        "Participant who has already cancelled registration tries to cancel again" {
            val cancelledParticipant = Participant(
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
                    ParticipantStatus.CANCELLED,
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
                resultingEither.shouldBeLeft()
                beLeft(
                    resultingEither.shouldBe(
                        Left(
                            RegistrationCancellationError.ParticipantAlreadyCancelled
                        )
                    )
                )
                slot.isCaptured.shouldBe(false)
            }
        }

        "Participant tries cancellation with incorrect token" {
            every { unit.eventCRUD.findByParticipantToken("incorrect-token") } returns None
            val slot = slot<ParticipantStatus>()
            every { unit.participantCRUD.updateStatus(1L, capture(slot)) } returns mockkClass(Participant::class).toOption()
            val resultingEither = unit("incorrect-token")
            assertSoftly {
                resultingEither.shouldBeLeft()
                beLeft(
                    resultingEither.shouldBe(
                        Left(
                            RegistrationCancellationError.EventNotFound
                        )
                    )
                )
                slot.isCaptured.shouldBe(false)
            }
        }
    }
}
