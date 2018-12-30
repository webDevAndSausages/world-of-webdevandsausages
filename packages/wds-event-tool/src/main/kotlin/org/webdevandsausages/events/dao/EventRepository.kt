package org.webdevandsausages.events.dao

import arrow.core.Option
import arrow.effects.IO
import meta.tables.Event
import org.webdevandsausages.events.dto.EventDto

object EventRepository {
    fun findAllWithParticipants(status: String?): IO<List<EventDto>?> = IO { EventCRUD.findAllWithParticipants(status) }

    fun findByIdOrLatest(id: Long? = null): Option<EventDto> = EventCRUD.findByIdOrLatest(id)

    fun update(id: Long?, updates: EventUpdates): Option<Int> = EventCRUD.update(id, updates)
}

val EventRepository.field get() = Event.EVENT