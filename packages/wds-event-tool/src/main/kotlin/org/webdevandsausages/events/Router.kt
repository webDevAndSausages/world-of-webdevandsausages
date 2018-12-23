package org.webdevandsausages.events

import arrow.core.Either
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
import org.http4k.core.Method.POST
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
import org.webdevandsausages.events.controllers.CreateRegistrationController
import org.webdevandsausages.events.controllers.EventError
import org.webdevandsausages.events.controllers.GetCurrentEventController
import org.webdevandsausages.events.controllers.GetEventByIdController
import org.webdevandsausages.events.controllers.GetEventsController
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.dto.EventsOutDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.utils.WDSJackson.auto

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

        "/api/1.0/events"
                meta {
                    summary = "Get all events and participants, with option to filter by status"
                    queries += optionalStatusQuery
                } bindContract GET to handleGetEvents(),

        "/api/1.0/events" / eventIdParam
                meta {
                    summary = "Get event by id"
                } bindContract GET to ::handleGetEventById,

        "/api/1.0/events" / eventIdParam / "registrations"
            meta {
            summary = "Register user"
            receiving(registrationRequestLens)
            returning("User has been registered to the event." to Status.OK)
            returning("The event is closed or non-existent." to Status.BAD_REQUEST)
            returning("The email is already registered." to Status.BAD_REQUEST)

        } bindContract POST to ::handleRegistration,

        "/api/1.0/events/latest"
                meta {
                    summary = "Get latest publishable event"
                }
                bindContract GET to handleGetCurrentEvent()
    )

    private fun ok() = { _: Request -> Response(OK) }

    private val errorResponseLens = Body.auto<ErrorOutDto>().toLens()

    private fun handleErrorResponse(message: String, code: ErrorCode, status: Status): Response {
        return errorResponseLens(ErrorOutDto(message, code), Response(status))
    }

    private val EventsLens = Body.auto<EventsOutDto>().toLens()

    private fun handleGetEvents(): HttpHandler = { req: Request ->
        val status = optionalStatusQuery(req).takeIf {
                v -> EventStatus.values()
            .filter { e ->
                e.name == v?.toUpperCase()}.isNotEmpty()
        }
        EventsLens(
            EventsOutDto(getEvents(status)),
            Response(Status.OK)
        )
    }

    private val EventLens = Body.auto<EventOutDto>().toLens()

    private fun handleGetCurrentEvent() = { _: Request ->
        EventLens(
            EventOutDto(getCurrentEvent()),
            Response(Status.OK)
        )
    }

    private fun handleGetEventById(id: Long): HttpHandler =
        { _: Request ->
            EventLens(
                EventOutDto(getEventById(id)),
                Response(Status.OK)
             )
        }

    private val registrationRequestLens = Body.auto<RegistrationInDto>().toLens()
    private val registrationResponseLens = Body.auto<RegistrationOutDto>().toLens()

    private fun handleRegistration(id: Long, _unusedButNeeded: String): HttpHandler = { req: Request ->
        // TODO: Validate body
        val registration = registrationRequestLens(req).apply {
            eventId = id
        }
        createRegistration(registration)?.let {
            when(it) {
                is Either.Left -> when (it.a){
                    is EventError.NotFound ->
                        handleErrorResponse(
                            "The event is closed or non-existent.",
                            ErrorCode.EVENT_CLOSED_OR_MISSING,
                            Status.BAD_REQUEST)
                    is EventError.AlreadyRegistered ->
                        handleErrorResponse(
                            "The email is already registered.",
                            ErrorCode.ALREADY_REGISTERED,
                            Status.BAD_REQUEST)
                }
                is Either.Right -> registrationResponseLens(
                    RegistrationOutDto(it.b),
                    Response(Status.OK))
            }
        }
    }
}