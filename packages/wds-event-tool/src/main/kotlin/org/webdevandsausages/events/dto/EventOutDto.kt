package org.webdevandsausages.events.dto

import meta.enums.EventStatus
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.controller.GetCurrentEvent.EventLens
import java.sql.Timestamp
import java.time.OffsetDateTime

data class EventOutDto(
    val id: Long,
    val name: String,
    val sponsor: String,
    val contact: String,
    val date: OffsetDateTime,
    val details: String,
    val location: String,
    val status: EventStatus,
    val maxParticipants: Int,
    val registrationOpens: Timestamp,
    val volume: Int,
    val sponsorLink: String? = null
) {
    constructor(event: EventDto) : this(
        event.event.id,
        event.event.name,
        event.event.sponsor,
        event.event.contact,
        event.event.date,
        event.event.details,
        event.event.location,
        event.event.status,
        event.event.maxParticipants,
        event.event.registrationOpens,
        event.event.volume,
        event.event.sponsorLink
    )
}
