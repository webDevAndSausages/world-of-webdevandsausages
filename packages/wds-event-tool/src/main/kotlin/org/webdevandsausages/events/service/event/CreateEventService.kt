package org.webdevandsausages.events.service.event

import arrow.core.Either
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.domain.EventError
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.EventInDto

class CreateEventService(val eventRepository: EventCRUD) {
    operator fun invoke(eventInDto: EventInDto): Either<EventError, EventDto> {

        if (!eventInDto.status.isInvisible && !eventRepository.findByIdOrLatest().isEmpty()) {
            return Either.Left(EventError.MultipleOpen)
        }

        return eventRepository.create(eventInDto).fold({
            Either.Left(EventError.DatabaseError)
        }, {
            Either.Right(it)
        })
    }
}