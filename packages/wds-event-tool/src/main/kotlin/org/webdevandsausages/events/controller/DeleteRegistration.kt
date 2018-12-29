package org.webdevandsausages.events.controller

import arrow.core.Either
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.string
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.CancelRegistrationInDto
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.service.CancelRegistrationService
import org.webdevandsausages.events.service.RegistrationCancellationError
import org.webdevandsausages.events.utils.WDSJackson.auto

object DeleteRegistration {
    private val cancelRegistrationRequestLens = Body.auto<CancelRegistrationInDto>().toLens()
    private val registrationToken = Path.string().of("token")

    fun route(
        deleteRegistration: CancelRegistrationService,
        handleErrorResponse: handleErrorResponse
    ): ContractRoute {

        @Suppress("UNUSED_PARAMETER")
        fun handleCancellation(token: String): HttpHandler = { req: Request ->

            val cancellation = cancelRegistrationRequestLens(req).apply {
                registrationToken = token
            }

            deleteRegistration(cancellation).let {
                when (it) {
                    is Either.Left -> when (it.a) {
                        is RegistrationCancellationError.EventNotFound, RegistrationCancellationError.EventClosed ->
                            handleErrorResponse(
                                "The event is closed or non-existent.",
                                ErrorCode.EVENT_CLOSED_OR_MISSING,
                                Status.NOT_FOUND
                            )
                        is RegistrationCancellationError.ParticipantNotFound ->
                            handleErrorResponse(
                                "The participant was not found with provided token",
                                ErrorCode.EVENT_CLOSED_OR_MISSING,
                                Status.NOT_FOUND
                            )
                        RegistrationCancellationError.DatabaseError ->
                            handleErrorResponse(
                                "A database error occurred.",
                                ErrorCode.DATABASE_ERROR,
                                Status.INTERNAL_SERVER_ERROR
                            )
                        RegistrationCancellationError.ParticipantAlreadyCancelled ->
                            handleErrorResponse(
                                "Participant was already cancelled",
                                ErrorCode.ALREADY_CANCELLED,
                                Status.UNPROCESSABLE_ENTITY
                            )
                        RegistrationCancellationError.ShouldNeverHappen ->
                            handleErrorResponse(
                                "Something weird happened. Should never happen, lol",
                                ErrorCode.SHOULD_NEVER_HAPPEN,
                                Status.INTERNAL_SERVER_ERROR
                            )
                    }
                    is Either.Right -> Router.cancelRegistrationResponseLens(
                        ParticipantDto(it.b),
                        Response(Status.OK)
                    )
                }
            }

        }

        return "events/registrations" / registrationToken meta {
            summary = "Cancel user registration"
            receiving(cancelRegistrationRequestLens)
            returning("Registration to the event has been cancelled." to Status.OK)
            returning("The event is closed or non-existent." to Status.NOT_FOUND)
            returning("Some internal error occurred" to Status.INTERNAL_SERVER_ERROR)
            returning("Participant was already cancelled" to Status.UNPROCESSABLE_ENTITY)
        } bindContract Method.DELETE to ::handleCancellation
    }
}
