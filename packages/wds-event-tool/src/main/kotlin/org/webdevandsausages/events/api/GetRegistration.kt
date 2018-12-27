package org.webdevandsausages.events.api

import arrow.core.Either
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.controllers.GetRegistrationController
import org.webdevandsausages.events.controllers.RegistrationError
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.utils.WDSJackson.auto

object GetRegistration {
    fun route(getRegistration: GetRegistrationController, handleErrorResponse: handleErrorResponse): ContractRoute {

        @Suppress("UNUSED_PARAMETER")
        fun handleGetRegistrationByToken(eventId: Long, _p2: String, verificationToken: String): HttpHandler =
            { _: Request ->
                getRegistration(eventId, verificationToken).let {
                    when (it) {
                        is Either.Left -> when (it.a) {
                            is RegistrationError.EventNotFound ->
                                handleErrorResponse(
                                    "The event does not exist.",
                                    ErrorCode.NOT_FOUND,
                                    Status.NOT_FOUND)
                            is RegistrationError.ParticipantNotFound ->
                                handleErrorResponse(
                                    "The participant was not found with that token.",
                                    ErrorCode.NOT_FOUND,
                                    Status.NOT_FOUND)
                            is RegistrationError.EventClosed ->
                                handleErrorResponse(
                                    "The event is closed.",
                                    ErrorCode.EVENT_CLOSED_OR_MISSING,
                                    Status.GONE)
                            is RegistrationError.DatabaseError ->
                                handleErrorResponse(
                                    "A database error occurred.",
                                    ErrorCode.DATABASE_ERROR,
                                    Status.INTERNAL_SERVER_ERROR)
                        }
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