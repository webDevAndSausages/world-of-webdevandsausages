package org.webdevandsausages.events.controllers

import arrow.core.Either
import meta.enums.EventStatus
import org.slf4j.Logger
import org.webdevandsausages.events.dao.EventUpdates
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.services.EventService
import org.webdevandsausages.events.services.field
import org.webdevandsausages.events.services.isOpenFeedbackStatus
import org.webdevandsausages.events.services.isOpenRegistrationStatus
import org.webdevandsausages.events.services.isVisibleStatus
import org.webdevandsausages.events.utils.hasPassed
import org.webdevandsausages.events.utils.threeDaysLater

interface GetEventsController {
    operator fun invoke(status: String?): List<EventDto>?
}

class GetEventsControllerImpl(val eventService: EventService) : GetEventsController {
    override fun invoke(status: String?): List<EventDto>?{
        return eventService.getEvents(status)
    }
}

interface GetCurrentEventController {
    operator fun invoke(): Either<EventError, EventDto>
}

/**
 * GetCurrentEventController should handle following updates
 *  1. If the event has begin, close registration
 *  2. If the event has happened, open feedback
 *  3. If the event happened three days ago, close feedback
 */

class GetCurrentEventControllerImpl(val eventService: EventService, val logger: Logger) : GetCurrentEventController {

    @Suppress("UNCHECKED_CAST")
    private fun openEvent(data: EventDto): Either<EventError, EventDto> {
        logger.info("Opening event ${data.event.name}")
        eventService.update(
            data.event.id,
            listOf(Pair(eventService.field.STATUS, EventStatus.OPEN)) as EventUpdates)
        return complete()
    }

    @Suppress("UNCHECKED_CAST")
    private fun closeRegistration(data: EventDto): Either<EventError, EventDto> {
        logger.info("Closing registration for event ${data.event.name}")
        eventService.update(
            data.event.id,
            listOf(Pair(eventService.field.STATUS, EventStatus.CLOSED_WITH_FEEDBACK)) as EventUpdates)
        return complete()
    }

    @Suppress("UNCHECKED_CAST")
    private fun closeFeedback(data: EventDto): Either<EventError, EventDto> {
        logger.info("Closing feedback for event ${data.event.name}")
        eventService.update(
            data.event.id,
            listOf(Pair(eventService.field.STATUS, EventStatus.CLOSED)) as EventUpdates)
        return complete()
    }

    private fun complete(): Either<EventError, EventDto> = eventService.getByIdOrLatest().fold({
            Either.Left(EventError.NotFound)
        }, {
            Either.Right(it)
        })

    override fun invoke(): Either<EventError, EventDto> {
        val result = eventService.getByIdOrLatest()
        return result.fold(
            { Either.Left(EventError.NotFound) },
            {
                val status = it.event.status
                val registrationOpens = it.event.registrationOpens
                val date = it.event.date

                return when {
                    status.isVisibleStatus && registrationOpens.hasPassed -> openEvent(it)
                    status.isOpenRegistrationStatus && date.hasPassed -> closeRegistration(it)
                    status.isOpenFeedbackStatus && date.threeDaysLater -> closeFeedback(it)
                    else -> Either.Right(it)
                }
            }
          )
    }
}

interface GetEventByIdController {
    operator fun invoke(eventId: Long): Either<EventError, EventDto>
}

class GetEventByIdControllerImpl(val eventService: EventService) : GetEventByIdController {
    override fun invoke(eventId: Long): Either<EventError, EventDto> {
        return eventService.getByIdOrLatest(eventId).fold({
            Either.Left(EventError.NotFound)
        }, {
            Either.Right(it)
        })
    }
}