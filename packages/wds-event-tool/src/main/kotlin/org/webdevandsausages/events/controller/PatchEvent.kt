package org.webdevandsausages.events.controller

import arrow.core.Either
import org.http4k.contract.ContractRoute
import org.http4k.contract.ContractRouteSpec1
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.*
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.EventInDto
import org.webdevandsausages.events.error.toResponse
import org.webdevandsausages.events.service.UpdateEventService
import org.webdevandsausages.events.utils.WDSJackson.auto

object PatchEvent {
    private val eventUpdateRequestLens = Body.auto<EventInDto>().toLens()

    fun route(createEvent: UpdateEventService): ContractRoute {

        fun handlepatchEvent(eventId: Long): HttpHandler = { req: Request ->
            createEvent(eventUpdateRequestLens(req)).let { reg ->
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
            returning( Status.OK to "Event updated.")
            returning(Status.NOT_FOUND to "Event was not found")
            returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
        } bindContract Method.PATCH to ::handlepatchEvent
    }
}
