package org.webdevandsausages.events.controller

import arrow.core.Either
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.error.RegistrationError
import org.webdevandsausages.events.error.toResponse
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.service.GetRegistrationService

object GetRegistration {
    fun route(getRegistration: GetRegistrationService): ContractRoute {

        @Suppress("UNUSED_PARAMETER")
        fun handleGetRegistrationByToken(eventId: Long, _p2: String, verificationToken: String): HttpHandler =
            { _: Request ->
                getRegistration(eventId, verificationToken).let {
                    when (it) {
                        is Either.Left -> it.a.toResponse()
                        is Either.Right -> Router.registrationResponseLens(
                            RegistrationOutDto(it.b),
                            Response(Status.OK)
                            )
                    }
                }
            }

        return "events" / Router.eventIdParam / "registrations" / Router.verificationTokenParam meta {
            summary = "Get registration by verification token"
            returning( Status.OK to "Registration found.")
            returning(Status.NOT_FOUND to "The event does not exist.")
            returning(Status.NOT_FOUND to "The participant was not found with that token.")
            returning(Status.GONE to "The event is closed.")
            returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
        } bindContract GET to ::handleGetRegistrationByToken
    }
}