package org.webdevandsausages.events

import org.http4k.contract.*
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.CorsPolicy
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.lens.Header
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.long
import org.http4k.lens.string
import org.http4k.routing.ResourceLoader
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.webdevandsausages.events.config.Secrets
import org.webdevandsausages.events.controller.*
import org.webdevandsausages.events.dto.*
import org.webdevandsausages.events.service.*
import org.webdevandsausages.events.utils.WDSJackson.auto

typealias handleErrorResponse = (message: String, code: ErrorCode, status: Status) -> Response

class Router(
    val getEvents: GetEventsService,
    val getCurrentEvent: GetCurrentEventService,
    val getEventById: GetEventByIdService,
    val getRegistration: GetRegistrationService,
    val createRegistration: CreateRegistrationService,
    val cancelRegistration: CancelRegistrationService,
    val createEvent: CreateEventService
) {

    operator fun invoke(secrets: Secrets?): RoutingHttpHandler {
        return DebuggingFilters
            .PrintRequestAndResponse()
            .then(ServerFilters.Cors(CorsPolicy(listOf("*"), listOf("wds-key"), listOf(Method.OPTIONS, GET, POST, DELETE, PUT))))
            .then(ServerFilters.CatchLensFailure)
            .then(
                routes(
                    "/api/1.0/" bind contract(
                        // Automatic Swagger
                        OpenApi(ApiInfo("Event tool api", "v1.0"), Jackson),
                        "/api-docs",
                        ApiKey(
                            Header.required("wds-key"),
                            { key: String -> key == secrets?.WDSApiKey ?: "wds-secret" }),
                        *getApiRoutes().toTypedArray()
                    ),
                    "/admin-api/" bind contract(
                        // Automatic Swagger
                        OpenApi(
                            ApiInfo(
                                "Event tool admin api",
                                "v1.0",
                                "No security as it will not be open to internet"
                            ), Jackson
                        ),
                        "/api-docs",
                        NoSecurity,
                        *getAdminApiRoutes().toTypedArray()
                    ),
                    "/" bind static(ResourceLoader.Classpath("public"))
                )
            )

    }

    private fun getAdminApiRoutes() = listOf(
        "/{any:.*}" bindContract OPTIONS to ok(),
        PostEvent.route(createEvent),
        AdminGetEventInfo.route(getEventById)
    )

    private fun getApiRoutes() = listOf(
        "/{any:.*}" bindContract OPTIONS to ok(),
        GetEvents.route(getEvents),
        GetEvent.route(getEventById),
        GetCurrentEvent.route(getCurrentEvent),
        GetRegistration.route(getRegistration),
        PostRegistration.route(createRegistration),
        DeleteRegistration.route(cancelRegistration)
    )

    private fun ok() = { _: Request -> Response(OK) }

    companion object {
        val eventIdParam = Path.long().of("id")
        val optionalStatusQuery = Query.optional("status")
        val verificationTokenParam = Path.string().of("verificationToken")
        val registrationResponseLens = Body.auto<RegistrationOutDto>().toLens()
        val cancelRegistrationResponseLens = Body.auto<ParticipantDto>().toLens()
        val eventResponseLens = Body.auto<EventDto>().toLens()
        val errorResponseLens = Body.auto<ErrorOutDto>().toLens()
    }
}
