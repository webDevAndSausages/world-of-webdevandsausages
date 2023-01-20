package org.webdevandsausages.events.service.event

import arrow.core.Either
import arrow.core.Some
import meta.enums.EventStatus
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.domain.EventError
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.EventUpdateInDto
import org.webdevandsausages.events.utils.createLogger

/**
 * GetCurrentEventService should handle following updates
 *  1. If the event has begin, close registration
 *  2. If the event has happened, open feedback
 *  3. If the event happened three days ago, close feedback
 */

class GetCurrentEventService(val eventCRUD: EventCRUD) {
    val logger = createLogger()

    @Suppress("UNCHECKED_CAST")
    private fun openEvent(data: EventDto): Either<EventError, EventDto> {
        logger.info("Opening event ${data.event.name}")
        val latestEvent = eventCRUD.findByIdOrLatest()

        if (latestEvent is Some && latestEvent.t.event.id != data.event.id) {
            logger.error("Cannot open event ${data.event.name} when another one is visible")
            return Either.Left(EventError.MultipleOpen)
        }

        eventCRUD.update(
            data.event.id,
            EventUpdateInDto(status = EventStatus.OPEN)
        )
        return getLatest()
    }

    @Suppress("UNCHECKED_CAST")
    private fun closeRegistration(data: EventDto): Either<EventError, EventDto> {
        logger.info("Closing registration for event ${data.event.name}")
        eventCRUD.update(
            data.event.id,
            EventUpdateInDto(status = EventStatus.CLOSED_WITH_FEEDBACK)
        )
        return getLatest()
    }

    @Suppress("UNCHECKED_CAST")
    private fun closeFeedback(data: EventDto): Either<EventError, EventDto> {
        logger.info("Closing feedback for event ${data.event.name}")
        eventCRUD.update(
            data.event.id,
            EventUpdateInDto(status = EventStatus.CLOSED)
        )
        return getLatest()
    }

    private fun getLatest(): Either<EventError, EventDto> = eventCRUD.findByIdOrLatest().fold({
        Either.Left(EventError.NotFound)
    }, {
        Either.Right(it)
    })

    operator fun invoke(): Either<EventError, EventDto> {
        val result = getLatest()
        return result.fold(
            { Either.Left(EventError.NotFound) },
            {
                val status = it.event.status
                val registrationOpens = it.event.registrationOpens
                val date = it.event.date

                return Either.Right(it)
                // TODO: These automatic status changes need to be tested properly before rushing to production
                // Currently this event will disappear on the day of the event as it's no longer open and visible.
                // Requires end-to-end testing.

                /* return when {
                    status.isVisibleStatus && registrationOpens.hasPassed -> openEvent(it)
                    status.canRegister && date.hasPassed -> closeRegistration(it)
                    status.isOpenFeedbackStatus && date.threeDaysLater -> closeFeedback(it)
                    else -> Either.Right(it)
                } */
            }
        )
    }
}
