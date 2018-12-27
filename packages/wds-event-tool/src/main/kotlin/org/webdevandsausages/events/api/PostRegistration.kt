package org.webdevandsausages.events.api

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
import org.http4k.lens.long
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.controllers.CreateRegistrationController
import org.webdevandsausages.events.controllers.EventError
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.utils.Read
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.webdevandsausages.events.utils.parse

object PostRegistration {
    private val registrationRequestLens = Body.auto<RegistrationInDto>().toLens()
    private val eventIdParam = Path.long().of("id")

    fun route(
        createRegistration: CreateRegistrationController,
        handleErrorResponse: handleErrorResponse
    ): ContractRoute {

        @Suppress("UNUSED_PARAMETER")
        fun handleRegistration(id: Long, _p2: String): HttpHandler = { req: Request ->

            val registration = registrationRequestLens(req).apply {
                eventId = id
            }
            val validation = parse(Read.emailRead, registration.email)
            validation.fold(
                {
                    handleErrorResponse(
                        "The email address is not valid",
                        ErrorCode.INVALID_EMAIL,
                        Status.UNPROCESSABLE_ENTITY)
                },
                {
                    createRegistration(registration).let {
                        when (it) {
                            is Either.Left -> when (it.a) {
                                is EventError.NotFound ->
                                    handleErrorResponse(
                                        "The event is closed or non-existent.",
                                        ErrorCode.EVENT_CLOSED_OR_MISSING,
                                        Status.NOT_FOUND)
                                is EventError.AlreadyRegistered ->
                                    handleErrorResponse(
                                        "The email is already registered.",
                                        ErrorCode.ALREADY_REGISTERED,
                                        Status.BAD_REQUEST)
                                is EventError.DatabaseError ->
                                    handleErrorResponse(
                                        "A database error occurred.",
                                        ErrorCode.DATABASE_ERROR,
                                        Status.INTERNAL_SERVER_ERROR)
                            }
                            is Either.Right -> Router.registrationResponseLens(
                                RegistrationOutDto(it.b),
                                Response(Status.CREATED))
                        }
                    }
                })
        }

        return "events" / eventIdParam / "registrations" meta {
            summary = "Register user"
            receiving(registrationRequestLens)
            returning("User has been registered to the event." to Status.CREATED)
            returning("The event is closed or non-existent." to Status.NOT_FOUND)
            returning("The email is already registered." to Status.BAD_REQUEST)
            returning("A database error occurred." to Status.INTERNAL_SERVER_ERROR)
            returning("The email address is not valid" to Status.UNPROCESSABLE_ENTITY)
        } bindContract Method.POST to ::handleRegistration
    }
}