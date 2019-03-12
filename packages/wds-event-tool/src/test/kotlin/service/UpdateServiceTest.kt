package service

import arrow.core.toOption
import io.kotlintest.Description
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import meta.enums.EventStatus
import meta.tables.Event.EVENT
import meta.tables.pojos.Event
import org.webdevandsausages.events.dao.EventUpdates
import org.webdevandsausages.events.dto.EventUpdateInDto
import org.webdevandsausages.events.service.UpdateEventService
import java.sql.Timestamp
import java.time.LocalDate

class UpdateServiceTest : StringSpec() {
    lateinit var unit: UpdateEventService

    override fun beforeTest(description: Description) {
        unit = UpdateEventService(
            eventRepository = mockk(relaxed = true)
        )
    }

    init {
        "Test update event input record construction with all fields" {
            val slot = slot<EventUpdates>()
            every { unit.eventRepository.update(1L, capture(slot)) } returns (mockk<Event>(relaxed = true)).toOption()

            unit.invoke(
                1L, EventUpdateInDto(
                    name = "testName",
                    maxParticipants = 15,
                    sponsor = "testSponsor",
                    contact = "testContact",
                    date = Timestamp.valueOf(LocalDate.of(2019, 1, 1).atStartOfDay()),
                    details = "testDetails",
                    location = "testLocation",
                    status = EventStatus.PLANNING,
                    registrationOpens = Timestamp.valueOf(LocalDate.of(2018, 12, 24).atStartOfDay()),
                    volume = 12,
                    sponsorLink = "testSponsorLink"
                )
            )

            slot.captured.shouldContainExactlyInAnyOrder(
                listOf(
                    EVENT.NAME to "testName",
                    EVENT.MAX_PARTICIPANTS to 15,
                    EVENT.SPONSOR to "testSponsor",
                    EVENT.CONTACT to "testContact",
                    EVENT.DATE to Timestamp.valueOf(LocalDate.of(2019, 1, 1).atStartOfDay()),
                    EVENT.DETAILS to "testDetails",
                    EVENT.LOCATION to "testLocation",
                    EVENT.STATUS to EventStatus.PLANNING,
                    EVENT.REGISTRATION_OPENS to Timestamp.valueOf(LocalDate.of(2018, 12, 24).atStartOfDay()),
                    EVENT.VOLUME to 12,
                    EVENT.SPONSOR_LINK to "testSponsorLink"
                )
            )
        }

        "Test update event input record construction with partial input" {
            // Test only partial update
            val slot = slot<EventUpdates>()
            every { unit.eventRepository.update(1L, capture(slot)) } returns (mockk<Event>(relaxed = true)).toOption()

            unit.invoke(
                1L, EventUpdateInDto(
                    maxParticipants = 15,
                    registrationOpens = Timestamp.valueOf(LocalDate.of(2018, 12, 24).atStartOfDay()),
                    sponsorLink = "testSponsorLink"
                )
            )

            slot.captured.shouldContainExactlyInAnyOrder(
                listOf(
                    EVENT.MAX_PARTICIPANTS to 15,
                    EVENT.REGISTRATION_OPENS to Timestamp.valueOf(LocalDate.of(2018, 12, 24).atStartOfDay()),
                    EVENT.SPONSOR_LINK to "testSponsorLink"
                )
            )
        }
    }
}
