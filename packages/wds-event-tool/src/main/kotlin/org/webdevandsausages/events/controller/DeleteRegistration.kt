package org.webdevandsausages.events.controller

import arrow.core.Either
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.string
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.error.RegistrationCancellationError
import org.webdevandsausages.events.error.toResponse
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.service.CancelRegistrationService

object DeleteRegistration {
    private val registrationToken = Path.string().of("token")

    fun route(deleteRegistration: CancelRegistrationService): ContractRoute {

        @Suppress("UNUSED_PARAMETER")
        fun handleCancellation(token: String): HttpHandler = { _: Request ->

            deleteRegistration(token).let {
                when (it) {
                    is Either.Left -> it.a.toResponse()
                    is Either.Right -> Router.cancelRegistrationResponseLens(
                        it.b,
                        Response(Status.OK)
                    )
                }
            }

        }

        return "events/registrations" / registrationToken meta {
            summary = "Cancel user registration"
            returning(Status.OK to "Registration to the event has been cancelled.")
            returning(Status.NOT_FOUND to "The event is closed or non-existent.")
            returning(Status.INTERNAL_SERVER_ERROR to "Some internal error occurred")
            returning(Status.UNPROCESSABLE_ENTITY to "Participant was already cancelled")
        } bindContract Method.DELETE to ::handleCancellation
    }
}
