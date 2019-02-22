package org.webdevandsausages.events.dto

import meta.enums.EventStatus
import meta.tables.pojos.Event
import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import kotlin.math.max

data class EventInDto(
    val name: String,
    val sponsor: String,
    val contact: String,
    val date: Timestamp,
    val details: String,
    val location: String,
    val status: EventStatus = EventStatus.PLANNING,
    val maxParticipants: Int,
    val registrationOpens: Timestamp,
    val createdOn: Timestamp,
    val updatedOn: Timestamp
)
