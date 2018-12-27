package org.webdevandsausages.events.api

import meta.enums.EventStatus
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.controllers.GetEventsController
import org.webdevandsausages.events.dto.EventsOutDto
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.http4k.core.Method.GET
import org.webdevandsausages.events.Router

object GetEvents {
    private val EventsLens = Body.auto<EventsOutDto>().toLens()

    fun route(getEvents: GetEventsController): ContractRoute {

        fun handleGetEvents(): HttpHandler = { req: Request ->
            val status = Router.optionalStatusQuery(req).takeIf {
                    v -> EventStatus.values()
                .filter { e ->
                    e.name == v?.toUpperCase() }.isNotEmpty()
            }
            EventsLens(
                EventsOutDto(getEvents(status)),
                Response(Status.OK)
                )
        }

        return "events" meta {
            summary = "Get all events and participants, with option to filter by status"
            queries += Router.optionalStatusQuery
        } bindContract GET to handleGetEvents()
    }
}