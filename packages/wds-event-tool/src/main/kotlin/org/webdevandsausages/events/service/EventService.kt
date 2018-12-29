package org.webdevandsausages.events.service

import arrow.core.Either
import meta.enums.EventStatus
import org.slf4j.Logger
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.EventUpdates
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dao.field
import org.webdevandsausages.events.utils.hasPassed
import org.webdevandsausages.events.utils.threeDaysLater

sealed class EventError {
    object NotFound : EventError()
    object AlreadyRegistered : EventError()
    object DatabaseError : EventError()
}

interface GetEventsService {
    operator fun invoke(status: String?): List<EventDto>?q
}

class GetEventsServiceImpl(val eventCRUD: EventCRUD) : GetEventsService {
    override fun invoke(status: String?): List<EventDto>? {
        return eventCRUD.findAllWithParticipants(status)
    }
}

interface GetCurrentEventService {
    operator fun invoke(): Either<EventError, EventDto>
}

/**
 * GetCurrentEventService should handle following updates
 *  1. If the event has begin, close registration
 *  2. If the event has happened, open feedback
 *  3. If the event happened three days ago, close feedback
 */

class GetCurrentEventServiceImpl(val eventCRUD: EventCRUD, val logger: Logger) : GetCurrentEventService {

    @Suppress("UNCHECKED_CAST")
    private fun openEvent(data: EventDto): Either<EventError, EventDto> {
        logger.info("Opening event ${data.event.name}")
        EventCRUD.update(
            data.event.id,
            listOf(Pair(eventCRUD.field.STATUS, EventStatus.OPEN)) as EventUpdates)
        return getLatest()
    }

    @Suppress("UNCHECKED_CAST")
    private fun closeRegistration(data: EventDto): Either<EventError, EventDto> {
        logger.info("Closing registration for event ${data.event.name}")
        eventCRUD.update(
            data.event.id,
            listOf(Pair(eventCRUD.field.STATUS, EventStatus.CLOSED_WITH_FEEDBACK)) as EventUpdates)
        return getLatest()
    }

    @Suppress("UNCHECKED_CAST")
    private fun closeFeedback(data: EventDto): Either<EventError, EventDto> {
        logger.info("Closing feedback for event ${data.event.name}")
        eventCRUD.update(
            data.event.id,
            listOf(Pair(eventCRUD.field.STATUS, EventStatus.CLOSED)) as EventUpdates)
        return getLatest()
    }

    private fun getLatest(): Either<EventError, EventDto> = eventCRUD.findByIdOrLatest().fold({
            Either.Left(EventError.NotFound)
        }, {
            Either.Right(it)
        })

    override fun invoke(): Either<EventError, EventDto> {
        val result = getLatest()
        return result.fold(
            { Either.Left(EventError.NotFound) },
            {
                val status = it.event.status
                val registrationOpens = it.event.registrationOpens
                val date = it.event.date

                return when {
                    status.isVisibleStatus && registrationOpens.hasPassed -> openEvent(it)
                    status.canRegister && date.hasPassed -> closeRegistration(it)
                    status.isOpenFeedbackStatus && date.threeDaysLater -> closeFeedback(it)
                    else -> Either.Right(it)
                }
                }
            )
    }
}

interface GetEventByIdService {
    operator fun invoke(eventId: Long): Either<EventError, EventDto>
}

class GetEventByIdServiceImpl(val eventCRUD: EventCRUD) : GetEventByIdService {
    override fun invoke(eventId: Long): Either<EventError, EventDto> {
        return eventCRUD.findByIdOrLatest(eventId).fold({
            Either.Left(EventError.NotFound)
        }, {
            Either.Right(it)
        })
    }
}

val EventStatus.isVisibleStatus get() = this == EventStatus.VISIBLE
val EventStatus.isNotFull get() = this == EventStatus.OPEN
val EventStatus.isWithWaitList get() = this == EventStatus.OPEN_WITH_WAITLIST
val EventStatus.canRegister get() = this == EventStatus.OPEN || this == EventStatus.OPEN_WITH_WAITLIST
val EventStatus.isOpenFeedbackStatus get() = this == EventStatus.CLOSED_WITH_FEEDBACK
val EventStatus.isInvisible get() = this == EventStatus.PLANNING || this == EventStatus.CLOSED || this == EventStatus.CANCELLED
