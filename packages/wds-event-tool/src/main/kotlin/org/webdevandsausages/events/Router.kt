package org.webdevandsausages.events

import org.http4k.contract.ApiInfo
import org.http4k.contract.ApiKey
import org.http4k.contract.OpenApi
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
import org.webdevandsausages.events.controller.DeleteRegistration
import org.webdevandsausages.events.controller.GetCurrentEvent
import org.webdevandsausages.events.controller.GetEvent
import org.webdevandsausages.events.controller.GetEvents
import org.webdevandsausages.events.controller.GetRegistration
import org.webdevandsausages.events.controller.PostRegistration
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.service.CancelRegistrationService
import org.webdevandsausages.events.service.CreateRegistrationService
import org.webdevandsausages.events.service.GetCurrentEventService
import org.webdevandsausages.events.service.GetEventByIdService
import org.webdevandsausages.events.service.GetEventsService
import org.webdevandsausages.events.service.GetRegistrationService
import org.webdevandsausages.events.utils.WDSJackson.auto

typealias handleErrorResponse = (message: String, code: ErrorCode, status: Status) -> Response

class Router(
    val getEvents: GetEventsService,
    val getCurrentEvent: GetCurrentEventService,
    val getEventById: GetEventByIdService,
    val getRegistration: GetRegistrationService,
    val createRegistration: CreateRegistrationService,
    val cancelRegistration: CancelRegistrationService
) {

    operator fun invoke(secrets: Secrets?): RoutingHttpHandler {
        return DebuggingFilters
            .PrintRequestAndResponse()
            .then(ServerFilters.CatchLensFailure)
            .then(routes(
                "/api/1.0/" bind contract(
                // Automatic Swagger
                    OpenApi(ApiInfo("Event tool api", "v1.0"), Jackson),
                    "/api-docs",
                    ApiKey(Header.required("wds-key"), { key: String -> key == secrets?.WDSApiKey ?: "wds-secret"}),
                    *getApiRoutes().toTypedArray()
                    ),
                "/" bind static(ResourceLoader.Classpath("public"))
                ))
    }

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
        val errorResponseLens = Body.auto<ErrorOutDto>().toLens()
    }
}