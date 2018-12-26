package org.webdevandsausages.events.services
import arrow.core.Option
import meta.enums.EventStatus
import meta.tables.Event
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.EventUpdates
import org.webdevandsausages.events.dto.EventDto

sealed class UserRegistrationError {
    object EmailAlreadyTaken : UserRegistrationError()
    object UsernameAlreadyTaken : UserRegistrationError()
}

object EventService {
    fun getEvents(status: String?): List<EventDto>? {
        return EventCRUD.findAllWithParticipants(status)
    }

    fun getByIdOrLatest(id: Long? = null): Option<EventDto> {
        return EventCRUD.findByIdOrLatest(id)
    }

    fun update(id: Long?, updates: EventUpdates): Option<Int> {
        return EventCRUD.update(id, updates)
    }
}

val EventStatus.isVisibleStatus get() = this == EventStatus.VISIBLE
val EventStatus.isOpenRegistrationStatus get() = this == EventStatus.OPEN || this == EventStatus.OPEN_WITH_WAITLIST
val EventStatus.isOpenFeedbackStatus get() = this == EventStatus.CLOSED_WITH_FEEDBACK
val EventService.field get() = Event.EVENT
