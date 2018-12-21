package org.webdevandsausages.events.controllers

import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.services.EventService

interface GetEventsController {
    operator fun invoke(status: String?): List<EventDto>?
}

class GetEventsControllerImpl(val eventService: EventService) : GetEventsController {
    override fun invoke(status: String?): List<EventDto>?{
        return eventService.getEvents(status)
    }
}

interface GetCurrentEventController {
    operator fun invoke(): EventDto?
}

class GetCurrentEventControllerImpl(val eventService: EventService) : GetCurrentEventController {
    override fun invoke(): EventDto? {
        val data = eventService.getEventByIdOrLatest()
        // TODO: check if VISIBLE and registration_opens >= now, then change status to OPEN
        return data
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