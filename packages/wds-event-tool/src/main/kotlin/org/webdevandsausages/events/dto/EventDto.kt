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
    .indexOfFirst { p -> p.verificationToken == verificationToken }.let { if (it > -1) it + 1 else -1 }

// next number should be rounded down to thousandth if last number was odd, eg. 4001
fun List<Participant>.getNextOrderNumber(): Int {
    val lastNumber = maxBy { it.orderNumber }?.orderNumber ?: 0
    return (lastNumber - (lastNumber % 1000)) + 1000
}

fun List<Participant>.getNextOrderNumberInStatusGroup(status: ParticipantStatus): Int = filter { it.status == status }.size + 1

val EventDto.hasWaitListedParticipants: Boolean
    get() = participants.any { it.status == ParticipantStatus.WAIT_LISTED }
