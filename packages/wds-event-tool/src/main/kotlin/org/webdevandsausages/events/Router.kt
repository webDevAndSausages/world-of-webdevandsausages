package org.webdevandsausages.events

import org.http4k.contract.ApiInfo
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.core.Body
import org.http4k.core.Method.OPTIONS
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
import org.webdevandsausages.events.api.GetCurrentEvent
import org.webdevandsausages.events.api.GetEvent
import org.webdevandsausages.events.api.GetEvents
import org.webdevandsausages.events.api.PostRegistration
import org.webdevandsausages.events.controllers.CreateRegistrationController
import org.webdevandsausages.events.controllers.GetCurrentEventController
import org.webdevandsausages.events.controllers.GetEventByIdController
import org.webdevandsausages.events.controllers.GetEventsController
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto
import org.webdevandsausages.events.utils.WDSJackson.auto

typealias handleErrorResponse = (message: String, code: ErrorCode, status: Status) -> Response

class Router(
    val getEvents: GetEventsController,
    val getCurrentEvent: GetCurrentEventController,
    val getEventById: GetEventByIdController,
    val createRegistration: CreateRegistrationController
    ) {

    companion object {
        val eventIdParam = Path.long().of("id")
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
        GetEvents.route(getEvents),
        GetEvent.route(getEventById, handleErrorResponse),
        GetCurrentEvent.route(getCurrentEvent, handleErrorResponse),
        PostRegistration.route(createRegistration, handleErrorResponse)
    )

    private fun ok() = { _: Request -> Response(OK) }

    private val errorResponseLens = Body.auto<ErrorOutDto>().toLens()

    val handleErrorResponse = fun (message: String, code: ErrorCode, status: Status): Response {
        return errorResponseLens(ErrorOutDto(message, code), Response(status))
    }
}