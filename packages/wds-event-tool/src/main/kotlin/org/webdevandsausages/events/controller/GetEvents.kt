package org.webdevandsausages.events.controller

import meta.enums.EventStatus
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.http4k.core.Method.GET
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.service.GetEventsService

object GetEvents {
    private val EventsLens = Body.auto<List<EventOutDto>>().toLens()

    fun route(getEvents: GetEventsService): ContractRoute {

        fun handleGetEvents(): HttpHandler = { req: Request ->
            val status = Router.optionalStatusQuery(req).takeIf { v ->
                EventStatus.values()
                    .filter { e ->
                        e.name == v?.toUpperCase()
                    }.isNotEmpty()
            }
            EventsLens(
                getEvents(status).map { EventOutDto(it) },
                Response(Status.OK)
            )
        }

        return "events" meta {
            summary = "Get all events and participants, with option to filter by status"
            queries += Router.optionalStatusQuery
        } bindContract GET to handleGetEvents()
    }
}
