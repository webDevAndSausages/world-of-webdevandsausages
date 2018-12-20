package org.webdevandsausages.events.controllers

import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.services.EventService

interface GetEventsController {
    operator fun invoke(): List<EventDto>?
}

class GetEventsControllerImpl(val eventService: EventService) : GetEventsController {
    override fun invoke(): List<EventDto>?{
        return eventService.getEvents()
    }
}

interface GetCurrentEventController {
    operator fun invoke(): EventDto?
}

class GetCurrentEventControllerImpl(val eventService: EventService) : GetCurrentEventController {
    override fun invoke(): EventDto? {
        return eventService.getEventByIdOrLatest()
    }
}

interface GetEventByIdController {
    operator fun invoke(eventId: Long): EventDto?
}

class GetEventByIdControllerImpl(val eventService: EventService) : GetEventByIdController {
    override fun invoke(eventId: Long): EventDto? {
        return eventService.getEventByIdOrLatest(eventId)
    }
}