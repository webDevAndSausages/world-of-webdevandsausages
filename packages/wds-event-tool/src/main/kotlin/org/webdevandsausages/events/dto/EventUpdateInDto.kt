package org.webdevandsausages.events.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.CaseFormat
import com.markodevcic.kvalidation.ValidatorBase
import com.markodevcic.kvalidation.onError
import com.markodevcic.kvalidation.rules
import meta.enums.EventStatus
import meta.tables.Event.EVENT
import meta.tables.pojos.Event
import meta.tables.records.EventRecord
import org.http4k.core.Response
import org.jooq.TableField
import org.webdevandsausages.events.dao.EventUpdate
import org.webdevandsausages.events.dao.EventUpdates
import org.webdevandsausages.events.error.EventError
import org.webdevandsausages.events.error.toResponse
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.math.max
import kotlin.reflect.full.declaredMemberProperties

data class EventUpdateInDto(
    val name: String? = null,
    val sponsor: String? = null,
    val contact: String? = null,
    val date: Timestamp? = null,
    val details: String? = null,
    val location: String? = null,
    val status: EventStatus? = null,
    val maxParticipants: Int? = null,
    val registrationOpens: Timestamp? = null,
    val volume: Int? = null,
    val sponsorLink: String? = null
)

fun EventUpdateInDto.toEventUpdates(): EventUpdates =
    this.javaClass.kotlin.declaredMemberProperties.fold(
        listOf<EventUpdate>(),
        { acc: EventUpdates, i ->
            val value = i.get(this)
            if (value != null)
                acc.plus(
                    (EVENT.field(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, i.name)) to value)
                            as EventUpdate
                )
            else
                acc
        })


class EventUpdateInDtoValidator(consumer: EventUpdateInDto) : ValidatorBase<EventUpdateInDto>(consumer) {}

infix fun EventUpdateInDto.whenValid(task: () -> Response): Response {
    val validator = EventUpdateInDtoValidator(this)
    if (this.maxParticipants != null) {
        validator.forProperty { it.maxParticipants } rules {
            gt(0)
        } onError {
            errorMessage("Max participants must be greater than 0")
        }
    }

    if (this.name != null) {
        validator.forProperty { it.name } rules {
            length(255)
        } onError {
            errorMessage("Name max length is 255 chars")
        }
    }

    if (this.sponsor != null) {
        validator.forProperty { it.sponsor } rules {
            length(255)
        } onError {
            errorMessage("Sponsor max length is 255 chars")
        }
    }

    if (this.contact != null) {
        validator.forProperty { it.contact } rules {
            length(255)
        } onError {
            errorMessage("Contact max length is 255 chars")
        }
    }

    if (this.date != null) {
        validator.forProperty { it.date } rules {
            mustBe { it != null && it.after(Timestamp.valueOf(LocalDateTime.now())) }
        } onError {
            errorMessage("Date must be in the future")
        }
    }

    if (this.details != null) {
        validator.forProperty { it.details } rules {
            length(1024)
        } onError {
            errorMessage("Details max length is 1024 chars")
        }
    }

    if (this.location != null) {
        validator.forProperty { it.location } rules {
            length(255)
        } onError {
            errorMessage("Location max length is 255 chars")
        }
    }

    if (this.registrationOpens != null) {
        validator.forProperty { it.registrationOpens } rules {
            mustBe { it != null && it.after(Timestamp.valueOf(LocalDateTime.now())) }
        } onError {
            errorMessage("Registration opens must be in the future")
        }
    }

    if (this.sponsorLink != null) {
        validator.forProperty { it.sponsorLink } rules {
            length(255)
        } onError {
            errorMessage("SponsorLink max length is 256 chars")
        }
    }

    val validationResult = validator.validate()

    return when (validationResult.isValid) {
        true -> task()
        false -> EventError.ValidationError(validationResult.validationErrors.map { it.message }.joinToString("\n")).toResponse()
    }
}
