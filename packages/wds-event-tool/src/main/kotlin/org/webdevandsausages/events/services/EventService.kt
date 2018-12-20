package org.webdevandsausages.events.services
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dto.EventDto


object EventService {
    fun getEvents(status: String?): List<EventDto>? {
        return EventCRUD.findAllWithParticipants(status)
    }
    fun getEventByIdOrLatest(id: Long? = null): EventDto? {
        return EventCRUD.findEventByIdOrLatest(id)
    }
}