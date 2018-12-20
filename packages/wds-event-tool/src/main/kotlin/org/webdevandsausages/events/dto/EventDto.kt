package org.webdevandsausages.events.dto

import com.fasterxml.jackson.annotation.JsonUnwrapped
import meta.tables.pojos.Event
import meta.tables.pojos.Participant


data class EventDto(
    @JsonUnwrapped
    val event: Event,
    var participants: List<Participant> = listOf()
)