package org.webdevandsausages.events.error

import org.http4k.core.Response
import org.webdevandsausages.events.dto.ErrorCode
import org.http4k.core.Status
import org.webdevandsausages.events.Router.Companion.errorResponseLens
import org.webdevandsausages.events.dto.ErrorOutDto

sealed class EventError {
    object NotFound : EventError() {
        val message = "The event is closed or non-existent."
        val code = ErrorCode.EVENT_CLOSED_OR_MISSING
        val status = Status.NOT_FOUND
    }
    object AlreadyRegistered : EventError() {
        val message = "The email is already registered."
        val code = ErrorCode.ALREADY_REGISTERED
        val status = Status.BAD_REQUEST
    }
    object DatabaseError : EventError() {
        val message = "A database error occurred."
        val code = ErrorCode.DATABASE_ERROR
        val status = Status.INTERNAL_SERVER_ERROR
    }
}

fun EventError.toResponse(): Response = when(this) {
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
}