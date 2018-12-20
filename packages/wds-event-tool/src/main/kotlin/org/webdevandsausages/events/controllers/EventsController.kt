package org.webdevandsausages.events.controllers

import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.services.EventService

interface GetEventsController {
    operator fun invoke(): List<EventDto>?
}

class GetEventsControllerImpl(val eventService: EventService) : GetEventsController {
    override fun invoke(): List<EventDto>? = eventService.getEvents()
}