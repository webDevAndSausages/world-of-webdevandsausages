package service

import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.mockk
import meta.enums.EventStatus
import meta.tables.Event.EVENT
import meta.tables.records.EventRecord
import org.webdevandsausages.events.dto.EventUpdateInDto
import org.webdevandsausages.events.service.UpdateEventService
import java.sql.Timestamp
import java.time.*

class UpdateServiceTest : StringSpec() {
    lateinit var unit: UpdateEventService

    private val now = 1550160535168L
    private val fixedClock = Clock.fixed(Instant.ofEpochMilli(now), ZoneId.systemDefault())

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
                date = Timestamp.valueOf(LocalDateTime.now(fixedClock)),
                details = "testDetails",
                location = "testLocation",
                status = EventStatus.PLANNING,
                registrationOpens = Timestamp.valueOf(LocalDateTime.now(fixedClock)),
                volume = 12,
                sponsorLink = "testSponsorLink"
            )
            val record = EventRecord()
            record.from(eventUpdateInDto)
            record.get(EVENT.NAME).shouldBe("testName")
            record.get(EVENT.MAX_PARTICIPANTS).shouldBe(15)
            record.get(EVENT.SPONSOR).shouldBe("testSponsor")
            record.get(EVENT.CONTACT).shouldBe("testContact")
            record.get(EVENT.DATE).shouldBe(OffsetDateTime.now(fixedClock))
            record.get(EVENT.DETAILS).shouldBe("testDetails")
            record.get(EVENT.LOCATION).shouldBe("testLocation")
            record.get(EVENT.STATUS).shouldBe(EventStatus.PLANNING)
            record.get(EVENT.REGISTRATION_OPENS).shouldBe(Timestamp.valueOf(LocalDateTime.now(fixedClock)))
            record.get(EVENT.VOLUME).shouldBe(12)
            record.get(EVENT.SPONSOR_LINK).shouldBe("testSponsorLink")

        }
    }
}
