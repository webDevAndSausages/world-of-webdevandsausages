package org.webdevandsausages.events.dto

import meta.enums.EventStatus
import java.sql.Timestamp

data class EventOutDto(
    val id: Long,
    val name: String,
    val sponsor: String,
    val contact: String,
    val date: Timestamp,
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
