package org.webdevandsausages.events.services
import arrow.core.Option
import meta.enums.EventStatus
import meta.tables.Event
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.EventUpdates
import org.webdevandsausages.events.dto.EventDto

object EventService {
    fun getEvents(status: String?): List<EventDto>? = EventCRUD.findAllWithParticipants(status)

    fun getByIdOrLatest(id: Long? = null): Option<EventDto> = EventCRUD.findByIdOrLatest(id)

    fun update(id: Long?, updates: EventUpdates): Option<Int> = EventCRUD.update(id, updates)
}

val EventStatus.isVisibleStatus get() = this == EventStatus.VISIBLE
val EventStatus.isNotFull get() = this == EventStatus.OPEN
val EventStatus.isWithWaitList get() = this == EventStatus.OPEN_WITH_WAITLIST
val EventStatus.canRegister get() = this == EventStatus.OPEN || this == EventStatus.OPEN_WITH_WAITLIST
val EventStatus.isOpenFeedbackStatus get() = this == EventStatus.CLOSED_WITH_FEEDBACK
val EventStatus.isInvisible get() = this == EventStatus.PLANNING || this == EventStatus.CLOSED || this == EventStatus.CANCELLED
val EventService.field get() = Event.EVENT
