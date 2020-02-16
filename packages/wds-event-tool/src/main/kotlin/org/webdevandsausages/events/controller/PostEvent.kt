package org.webdevandsausages.events.controller

import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.string
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.EventInDto
import org.webdevandsausages.events.domain.toResponse
import org.webdevandsausages.events.service.CreateEventService

object PostEvent {
    private val eventCreateRequestLens =
        Body.string(ContentType.APPLICATION_JSON).map { json -> EventInDto.from(json) }.toLens()

    fun route(createEvent: CreateEventService): ContractRoute {

        fun handlePostEvent(): HttpHandler = { req: Request ->
            eventCreateRequestLens(req).fold(
                { e -> e.toResponse() },
                { r ->
                    createEvent(r).fold(
                        { err -> err.toResponse() },
                        { data ->
                            Router.eventResponseLens(
                                data,
                                Response(Status.CREATED)
                            )
                        }
                    )
                }
            )
        }

        return "/events" meta {
            summary = "Create new event"
            queries += Router.optionalStatusQuery
        } bindContract Method.POST to handlePostEvent()
    }
}
