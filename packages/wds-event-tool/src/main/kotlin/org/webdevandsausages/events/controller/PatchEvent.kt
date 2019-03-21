package org.webdevandsausages.events.controller

import arrow.core.Either
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.lens.string
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.EventUpdateInDto
import org.webdevandsausages.events.error.toResponse
import org.webdevandsausages.events.service.GetEventByIdService
import org.webdevandsausages.events.service.UpdateEventService

object PatchEvent {
    private val eventUpdateRequestLens =
        Body.string(ContentType.APPLICATION_JSON).map { json -> EventUpdateInDto.from(json) }.toLens()

    fun route(updateEvent: UpdateEventService, findByIdService: GetEventByIdService): ContractRoute {
        fun handlepatchEvent(eventId: Long): HttpHandler = lambda@{ req: Request ->
            /* TODO: When updating maxParticipants, this ticket
                   https://github.com/webDevAndSausages/world-of-webdevandsausages/issues/35 needs to be considered */
            val oldEvent = findByIdService(eventId)
            if (oldEvent is Either.Left) return@lambda oldEvent.a.toResponse()
            val eitherValidatedOrError = eventUpdateRequestLens(req)
            if (eitherValidatedOrError is Either.Left) return@lambda eitherValidatedOrError.a.toResponse()

            val validatedEventUpdateInDto = (eitherValidatedOrError as Either.Right).b
            updateEvent(eventId, validatedEventUpdateInDto).let { reg ->
                when (reg) {
                    is Either.Left -> reg.a.toResponse()
                    is Either.Right -> Router.eventResponseLens(
                        reg.b,
                        Response(Status.OK)
                    )
                }
            }
        }

        return "events" / Router.eventIdParam meta {
            summary = "Update event details"
            returning(Status.OK to "Event updated.")
            returning(Status.NOT_FOUND to "Event was not found")
            returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
            returning(Status.UNPROCESSABLE_ENTITY to "Validation error")
        } bindContract Method.PATCH to ::handlepatchEvent
    }
}
