package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.domain.toResponse
import org.webdevandsausages.events.service.registration.GetRegistrationService

object GetRegistration: ApiRouteWithGraphqlConfig {

    private var getRegistration: GetRegistrationService? = null

    operator fun invoke(getRegistration: GetRegistrationService): GetRegistration {
        this.getRegistration = getRegistration
        return this
    }


    @Suppress("UNUSED_PARAMETER")
    private fun handleGetRegistrationByToken(eventId: Long, _p2: String, verificationToken: String): HttpHandler =
        { _: Request ->
            getRegistration!!(eventId, verificationToken).fold(
                { it.toResponse() },
                { Router.registrationResponseLens(
                    RegistrationOutDto(it),
                    Response(Status.OK)
                )}
            )
        }

    override val route: ContractRoute = "/events" / Router.eventIdParam / "registrations" / Router.verificationTokenParam meta {
            summary = "Get registration by verification token"
            returning( Status.OK to "Registration found.")
            returning(Status.NOT_FOUND to "The event does not exist.")
            returning(Status.NOT_FOUND to "The participant was not found with that token.")
            returning(Status.GONE to "The event is closed.")
            returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
        } bindContract GET to ::handleGetRegistrationByToken

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        query("getRegistration") {
            resolver { eventId: Long, verificationToken: String ->
                getRegistration!!(eventId, verificationToken).fold(
                    { throw it },
                    { RegistrationOutDto(it) }
                )
            }
        }
    }
}