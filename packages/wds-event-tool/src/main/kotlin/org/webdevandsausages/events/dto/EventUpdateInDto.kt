package org.webdevandsausages.events.dto

import com.fasterxml.jackson.annotation.JsonProperty
import meta.enums.EventStatus
import meta.tables.pojos.Event
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.math.max

data class EventUpdateInDto(
    val name: String?,
    val sponsor: String?,
    val contact: String?,
    val date: Timestamp?,
    val details: String?,
    val location: String?,
    val status: EventStatus?,
    @JsonProperty("maxParticipants")
    val max_participants: Int?,
    val registrationOpens: Timestamp?,
    val volume: Int?,
    val sponsorLink: String?
)
