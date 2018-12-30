package org.webdevandsausages.events.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.instances.io.monad.monad
import arrow.instances.either.monad.flatten
import arrow.typeclasses.binding
import meta.enums.EventStatus
import org.slf4j.Logger
import org.webdevandsausages.events.dao.EventRepository
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

// attempt ensures that any errors are captured so unsafeRunSync can't lock
fun<A> IO<Either<EventError, A>>.runWithError(logger: Logger): Either<EventError, A> = this.attempt()
    .unsafeRunSync()
    .fold({ error ->
        logger.error("An error occurred fetching: ${error.message}")
        EventError.DatabaseError.left()
    }, { it -> it.right() })
    .flatten()

// fold the request into a command
data class GetEventsCommand(val status: String?)
typealias GetEvents = (status: String?) -> IO<List<EventDto>?>
typealias EventListIO = IO<Either<EventError, List<EventDto>>>
// this can be implemented with an object, which only needs to provide the getEvents dependency
interface GetEventsService {
    val getEvents: GetEvents

    fun GetEventsCommand.execute(): EventListIO {
        val cmd = this
        return IO.monad().binding {
            // here we can run multiple IOs, validation or any logic and flatter into one Either
            // without nested maps / flatMap
            getEvents(cmd.status).bind().let {
                if (it is List) it.right() else emptyList<EventDto>().right()
            }
        }.fix()
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

class GetCurrentEventServiceImpl(val eventRepository: EventRepository, val logger: Logger) : GetCurrentEventService {

    @Suppress("UNCHECKED_CAST")
    private fun openEvent(data: EventDto): Either<EventError, EventDto> {
        logger.info("Opening event ${data.event.name}")
        eventRepository.update(
            data.event.id,
            listOf(Pair(eventRepository.field.STATUS, EventStatus.OPEN)) as EventUpdates)
        return getLatest()
    }

    @Suppress("UNCHECKED_CAST")
    private fun closeRegistration(data: EventDto): Either<EventError, EventDto> {
        logger.info("Closing registration for event ${data.event.name}")
        eventRepository.update(
            data.event.id,
            listOf(Pair(eventRepository.field.STATUS, EventStatus.CLOSED_WITH_FEEDBACK)) as EventUpdates)
        return getLatest()
    }

    @Suppress("UNCHECKED_CAST")
    private fun closeFeedback(data: EventDto): Either<EventError, EventDto> {
        logger.info("Closing feedback for event ${data.event.name}")
        eventRepository.update(
            data.event.id,
            listOf(Pair(eventRepository.field.STATUS, EventStatus.CLOSED)) as EventUpdates)
        return getLatest()
    }

    private fun getLatest(): Either<EventError, EventDto> = eventRepository.findByIdOrLatest().fold({
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

class GetEventByIdServiceImpl(val eventRepository: EventRepository) : GetEventByIdService {
    override fun invoke(eventId: Long): Either<EventError, EventDto> {
        return eventRepository.findByIdOrLatest(eventId).fold({
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
