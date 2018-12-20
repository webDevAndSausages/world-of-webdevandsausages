package org.webdevandsausages.events

import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.CorsPolicy
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.webdevandsausages.events.controllers.GetEventsController
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.utils.WDSJackson.auto

class Router(
    val getEvents: GetEventsController
            ) {

    operator fun invoke(): RoutingHttpHandler {
        return DebuggingFilters
            .PrintRequestAndResponse()
            .then(ServerFilters.Cors(CorsPolicy.UnsafeGlobalPermissive))
            .then(ServerFilters.CatchLensFailure)
            .then(routes(*getRoutes().toTypedArray()))
    }

    private fun getRoutes() = listOf(
        "/{any:.*}" bind Method.OPTIONS to ok(),
        "/api/events" bind GET to handleGetEvents()
    )

    private fun ok() = { _: Request -> Response(OK) }

    private val EventsLens = Body.auto<EventsResponse>().toLens()
    data class EventsResponse(val events: List<EventDto>?)

    private fun handleGetEvents() = { req: Request ->
        EventsLens(
            EventsResponse(getEvents()),
            Response(Status.OK)
        )
    }

}