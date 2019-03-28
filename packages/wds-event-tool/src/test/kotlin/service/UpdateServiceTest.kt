package service

import arrow.core.toOption
import io.kotlintest.Description
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import meta.enums.EventStatus
import meta.tables.Event.EVENT
import meta.tables.records.EventRecord
import org.webdevandsausages.events.dao.EventUpdates
import org.webdevandsausages.events.dto.EventDto
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
        "Test input DTO to jooq record mapping" {
            val eventUpdateInDto = EventUpdateInDto(
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
            val record = EventRecord()
            record.from(eventUpdateInDto)
            record.get(EVENT.NAME).shouldBe("testName")
            record.get(EVENT.MAX_PARTICIPANTS).shouldBe(15)
            record.get(EVENT.SPONSOR).shouldBe("testSponsor")
            record.get(EVENT.CONTACT).shouldBe("testContact")
            record.get(EVENT.DATE).shouldBe(Timestamp.valueOf(LocalDate.of(2019, 1, 1).atStartOfDay()))
            record.get(EVENT.DETAILS).shouldBe("testDetails")
            record.get(EVENT.LOCATION).shouldBe("testLocation")
            record.get(EVENT.STATUS).shouldBe(EventStatus.PLANNING)
            record.get(EVENT.REGISTRATION_OPENS).shouldBe(Timestamp.valueOf(LocalDate.of(2018, 12, 24).atStartOfDay()))
            record.get(EVENT.VOLUME).shouldBe(12)
            record.get(EVENT.SPONSOR_LINK).shouldBe("testSponsorLink")

        }
    }
}
