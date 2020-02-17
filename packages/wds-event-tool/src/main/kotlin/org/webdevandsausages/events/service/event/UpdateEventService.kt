package org.webdevandsausages.events.service.event

import arrow.core.Either
import meta.enums.EventStatus
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.domain.EventError
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.EventUpdateInDto

class UpdateEventService(val eventRepository: EventCRUD) {
    operator fun invoke(eventId: Long, eventInDto: EventUpdateInDto): Either<EventError, EventDto> {

        val latestEvent = eventRepository.findByIdOrLatest();
        if (eventInDto.status != null && !eventInDto.status.isInvisible && !latestEvent.isEmpty() &&
            (latestEvent.orNull()?.event?.id == eventId && latestEvent.orNull()?.event?.status != EventStatus.VISIBLE
                    && latestEvent.orNull()?.event?.status != EventStatus.OPEN)
        ) {
            return Either.Left(EventError.MultipleOpen)
        }

        return eventRepository.update(eventId, eventInDto).fold({
            Either.Left(EventError.DatabaseError)
        }, {
            Either.Right(it)
        })
    }
}