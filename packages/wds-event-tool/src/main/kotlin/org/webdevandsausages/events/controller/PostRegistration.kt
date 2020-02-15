package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.long
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.error.toResponse
import org.webdevandsausages.events.service.CreateRegistrationService
import org.webdevandsausages.events.utils.Read
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.webdevandsausages.events.utils.parse

object PostRegistration: ApiRouteWithGraphqlConfig {
    private val registrationRequestLens = Body.auto<RegistrationInDto>().toLens()
    private val eventIdParam = Path.long().of("id")

    private var  createRegistration: CreateRegistrationService? = null

    operator fun invoke(createRegistration: CreateRegistrationService): PostRegistration {
        this.createRegistration = createRegistration
        return this
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleRegistration(id: Long, _p2: String): HttpHandler = { req: Request ->

        val registration = registrationRequestLens(req).apply {
            eventId = id
        }
        val validation = parse(Read.emailRead, registration.email)
        validation.fold(
            {
                Router.errorResponseLens(
                    ErrorOutDto("The email address is not valid",
                        ErrorCode.INVALID_EMAIL),
                    Response(Status.UNPROCESSABLE_ENTITY)
                )
            },
            {
                createRegistration!!(registration).fold(
                    { it.toResponse()},
                    { Router.registrationResponseLens(
                        RegistrationOutDto(it),
                        Response(Status.CREATED))}
                )
            })
    }

    override val route: ContractRoute = "/events" / eventIdParam / "registrations" meta {
            summary = "Register user"
            receiving(registrationRequestLens)
            returning(Status.CREATED to "User has been registered to the event.")
            returning(Status.NOT_FOUND to "The event is closed or non-existent.")
            returning(Status.BAD_REQUEST to "The email is already registered.")
            returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
            returning(Status.UNPROCESSABLE_ENTITY to "The email address is not valid")
        } bindContract Method.POST to ::handleRegistration

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        mutation("createRegistration") {
            resolver { eventId: Long, email: String, firstName: String?, lastName: String?, affiliation: String?, subscribe: Boolean? ->
                parse(Read.emailRead, email).fold(
                    {
                        throw Exception("The email address is not valid")
                    },
                    { _ ->
                        val registration = RegistrationInDto(
                            eventId = eventId,
                            email = email,
                            firstName = firstName,
                            lastName = lastName,
                            affiliation = affiliation,
                            subscribe = subscribe
                        )
                        createRegistration!!(registration).fold(
                            { throw it },
                            { RegistrationOutDto(it) }
                        )
                    })
            }
        }
    }
}
