package org.webdevandsausages.events

import CreateRegistrationService
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.security.ApiKeySecurity
import org.http4k.contract.security.NoSecurity
import org.http4k.core.Body
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.OPTIONS
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
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
import org.webdevandsausages.events.controller.*
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.graphql.GraphqlRouter
import org.webdevandsausages.events.graphql.createSchema
import org.webdevandsausages.events.service.*
import org.webdevandsausages.events.service.event.CreateEventService
import org.webdevandsausages.events.service.event.GetCurrentEventService
import org.webdevandsausages.events.service.event.GetEventByIdService
import org.webdevandsausages.events.service.event.GetEventsService
import org.webdevandsausages.events.service.event.UpdateEventService
import org.webdevandsausages.events.service.registration.CancelRegistrationService
import org.webdevandsausages.events.service.registration.GetRegistrationService
import org.webdevandsausages.events.utils.GetEvents
import org.webdevandsausages.events.utils.WDSJackson.auto

typealias handleErrorResponse = (message: String, code: ErrorCode, status: Status) -> Response

class Router(
    val getEvents: GetEventsService,
    val getCurrentEvent: GetCurrentEventService,
    val getEventById: GetEventByIdService,
    val getRegistration: GetRegistrationService,
    val createRegistration: CreateRegistrationService,
    val cancelRegistration: CancelRegistrationService,
    val createEvent: CreateEventService,
    val updateEvent: UpdateEventService,
    val createContact: CreateContactService,
    val getContactEmails: GetContactEmailsService,
    val emailService: EmailService,
    val createBlacklist: CreateBlacklistService,
    val unsubscribe: UnsubscribeEmailService
) {

    operator fun invoke(secrets: Secrets?): RoutingHttpHandler {
        return DebuggingFilters
            .PrintRequestAndResponse()
            .then(
                ServerFilters.Cors(
                    CorsPolicy(
                        origins = listOf("*"),
                        headers = listOf("wds-key", "content-type"),
                        methods = listOf(OPTIONS, GET, POST, DELETE, PUT)
                    )
                )
            )
            .then(ServerFilters.CatchLensFailure)
            .then(
                routes(
                    "/api/1.0" bind contract {
                        renderer = OpenApi3(ApiInfo("Event tool api", "v1.0"), Jackson)
                        descriptionPath = "/api-docs"
                        security = apiSecurity(secrets)
                        routes += getApiRoutes()
                    },
                    "/admin-api" bind contract {
                        renderer = OpenApi3(
                            ApiInfo(
                                "Event tool admin api",
                                "v1.0",
                                "No security as it will not be open to internet"
                            ), Jackson
                        )
                        descriptionPath = "/api-docs"
                        security = NoSecurity
                        routes += getAdminApiRoutes()
                    },
                    "/graphql" bind GraphqlRouter(getGraphqlSchemas()),
                    "/" bind static(ResourceLoader.Classpath("public"))
                )
            )

    }


    private fun apiSecurity(secrets: Secrets?) = ApiKeySecurity(
        Header.string().required("wds-key"),
        { key: String -> key == secrets?.WDSApiKey ?: "wds-secret" })


    private fun getGraphqlSchemas() = createSchema(
        listOf(
            GetCurrentEvent(getCurrentEvent),
            GetEvents(getEvents),
            GetEvent(getEventById),
            GetRegistration(getRegistration),
            PostRegistration(createRegistration),
            DeleteRegistration(cancelRegistration),
            GetUser(getRegistration)
        )
    )

    private fun getAdminApiRoutes() = listOf(
        "/{any:.*}" bindContract OPTIONS to ok(),
        PostEvent.route(createEvent),
        AdminGetEventInfo.route(getEventById),
        PatchEvent.route(updateEvent, getEventById),
        GetEvents(getEvents).route,
        GetCurrentEvent(getCurrentEvent).route,
        PostSpam(emailService, getContactEmails).route
    )

    private fun getApiRoutes() = listOf(
        "/{any:.*}" bindContract OPTIONS to ok(),
        GetEvents(getEvents).route,
        GetEvent(getEventById).route,
        GetCurrentEvent(getCurrentEvent).route,
        GetRegistration(getRegistration).route,
        PostRegistration(createRegistration).route,
        DeleteRegistration(cancelRegistration).route,
        GetUser(getRegistration).route,
        PostContact(createContact).route,
        PostBlacklist(createBlacklist).route,
        GetUnsubscribe(unsubscribe).route
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
