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
            returning("Registration found." to Status.OK)
            returning("The event does not exist." to Status.NOT_FOUND)
            returning("The participant was not found with that token." to Status.NOT_FOUND)
            returning("The event is closed." to Status.GONE)
            returning("A database error occurred." to Status.INTERNAL_SERVER_ERROR)
        } bindContract GET to ::handleGetRegistrationByToken
    }
}