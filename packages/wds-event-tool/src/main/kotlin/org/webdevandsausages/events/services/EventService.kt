package org.webdevandsausages.events.services
import meta.enums.EventStatus
import meta.tables.Event
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.EventUpdates
import org.webdevandsausages.events.dto.EventDto


object EventService {
    fun getEvents(status: String?): List<EventDto>? {
        return EventCRUD.findAllWithParticipants(status)
    }
    fun getEventByIdOrLatest(id: Long? = null): EventDto? {
        return EventCRUD.findEventByIdOrLatest(id)
    }
    fun updateEvent(id: Long?, updates: EventUpdates) {
        return EventCRUD.updateEvent(id, updates)
    }
}

val EventStatus.isVisibleStatus get() = this == EventStatus.VISIBLE
val EventStatus.isOpenRegistrationStatus get() = this == EventStatus.OPEN || this == EventStatus.OPEN_WITH_WAITLIST
val EventStatus.isOpenFeedbackStatus get() = this == EventStatus.CLOSED_WITH_FEEDBACK
val EventService.field get() = Event.EVENT


