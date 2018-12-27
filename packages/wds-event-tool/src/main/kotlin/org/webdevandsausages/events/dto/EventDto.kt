package org.webdevandsausages.events.dto

import com.fasterxml.jackson.annotation.JsonUnwrapped
import meta.enums.ParticipantStatus
import meta.tables.pojos.Event
import meta.tables.pojos.Participant

data class EventDto(
    @JsonUnwrapped
    val event: Event,
    var participants: List<Participant> = emptyList()
)

fun List<Participant>.getPosition(status: ParticipantStatus, verificationToken: String) = asSequence()
    .filter { p -> p.status == status }
    .sortedBy { p -> p.orderNumber }
    .indexOfFirst { p -> p.verificationToken == verificationToken } + 1