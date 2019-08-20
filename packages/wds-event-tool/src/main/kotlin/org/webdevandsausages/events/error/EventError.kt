package org.webdevandsausages.events.error

import org.http4k.core.Response
import org.webdevandsausages.events.dto.ErrorCode
import org.http4k.core.Status
import org.webdevandsausages.events.Router.Companion.errorResponseLens
import org.webdevandsausages.events.dto.ErrorOutDto

sealed class EventError(message: String = "", status: Status, code: ErrorCode) : WDSException(message, status, code) {
    object NotFound :
        EventError("The event is closed or non-existent.", Status.NOT_FOUND, ErrorCode.EVENT_CLOSED_OR_MISSING)

    object AlreadyRegistered :
        EventError("The email is already registered.", Status.BAD_REQUEST, ErrorCode.ALREADY_REGISTERED)

    object DatabaseError :
        EventError(
            "A database error occurred. Ensure the volume number is unique.",
            Status.INTERNAL_SERVER_ERROR,
            ErrorCode.DATABASE_ERROR
        )

    class ValidationError(message: String) :
        EventError(message, Status.UNPROCESSABLE_ENTITY, ErrorCode.VALIDATION_ERROR)

    object MultipleOpen :
        EventError("Only one event can be open at a time.", Status.UNPROCESSABLE_ENTITY, ErrorCode.VALIDATION_ERROR)
}

fun EventError.toResponse(): Response = when (this) {
    EventError.NotFound ->
        with(EventError.NotFound) {
            errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    EventError.AlreadyRegistered ->
        with(EventError.AlreadyRegistered) {
            errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    EventError.DatabaseError ->
        with(EventError.DatabaseError) {
            errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    EventError.MultipleOpen ->
        with(EventError.MultipleOpen) {
            errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    is EventError.ValidationError -> errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
}

