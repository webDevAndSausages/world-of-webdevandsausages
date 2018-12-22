package org.webdevandsausages.events

import meta.enums.EventStatus
import org.http4k.contract.ApiInfo
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.OPTIONS
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.CorsPolicy
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.lens.Path
import org.http4k.lens.long
import org.http4k.format.Jackson
import org.http4k.contract.OpenApi
import org.http4k.lens.Query
import org.http4k.routing.RoutingHttpHandler
import org.webdevandsausages.events.controllers.GetCurrentEventController
import org.webdevandsausages.events.controllers.GetEventByIdController
import org.webdevandsausages.events.controllers.GetEventsController
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.utils.WDSJackson.auto

class Router(
    val getEvents: GetEventsController,
    val getCurrentEvent: GetCurrentEventController,
    val getEventById: GetEventByIdController
    ) {

    companion object {
        val idParam = Path.long().of("id")
        val optionalStatusQuery = Query.optional("status")
    }

    operator fun invoke(): RoutingHttpHandler {
        return DebuggingFilters
            .PrintRequestAndResponse()
            .then(ServerFilters.Cors(CorsPolicy.UnsafeGlobalPermissive))
            .then(ServerFilters.CatchLensFailure)
            .then(contract(
                // Automatic Swagger
                OpenApi(ApiInfo("Event tool api", "v1.0"), Jackson),
                "/api-docs",
                *getRoutes().toTypedArray())
             )
    }

    private fun getRoutes() = listOf(
        "/{any:.*}" bindContract OPTIONS to ok(),

        "/api/1.0/events"
                meta {
                    summary = "Get all events and participants, with option to filter by status"
                    queries += optionalStatusQuery
                } bindContract GET to handleGetEvents(),

        "/api/1.0/events" /
                idParam
                meta {
                    summary = "Get event by id"
                } bindContract GET to ::handleGetEventById,

        "/api/1.0/events/latest"
                meta {
                    summary = "Get latest publishable event"
                }
                bindContract GET to handleGetCurrentEvent()
    )

    private fun ok() = { _: Request -> Response(OK) }

    private val EventsLens = Body.auto<EventsResponse>().toLens()
    data class EventsResponse(val events: List<EventDto>?)

    private fun handleGetEvents(): HttpHandler = { req: Request ->
        val status = optionalStatusQuery(req).takeIf {
                v -> EventStatus.values()
            .filter { e ->
                e.name == v?.toUpperCase()}.isNotEmpty()
        }
        EventsLens(
            EventsResponse(getEvents(status)),
            Response(Status.OK)
        )
    }

    private val EventLens = Body.auto<EventResponse>().toLens()
    data class EventResponse(val currentEvent: EventDto?)

    private fun handleGetCurrentEvent() = { _: Request ->
        EventLens(
            EventResponse(getCurrentEvent()),
            Response(Status.OK)
        )
    }

    private fun handleGetEventById(id: Long): HttpHandler =
        { _: Request ->
            EventLens(
                EventResponse(getEventById(id)),
                Response(Status.OK)
             )
        }


}