package org.webdevandsausages.events.service.event

import arrow.core.Either
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.domain.EventError
import org.webdevandsausages.events.dto.EventDto

class GetEventByIdService(val eventRepository: EventCRUD) {
    operator fun invoke(eventId: Long): Either<EventError, EventDto> {
        return eventRepository.findByIdOrLatest(eventId).fold({
            Either.Left(EventError.NotFound)
        }, {
            Either.Right(it)
        })
    }
}