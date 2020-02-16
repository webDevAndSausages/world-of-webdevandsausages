package org.webdevandsausages.events.service.event

import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dto.EventDto

class GetEventsService(val eventRepository: EventCRUD) {
    operator fun invoke(status: String?): List<EventDto> {
        return eventRepository.findAllWithParticipants(status)
    }
}