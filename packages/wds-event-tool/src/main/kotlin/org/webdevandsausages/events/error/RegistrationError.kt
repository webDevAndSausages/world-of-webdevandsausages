package org.webdevandsausages.events.error

import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto

sealed class RegistrationError {
    object EventNotFound : RegistrationError() {
        val message = "The event does not exist."
        val code = ErrorCode.NOT_FOUND
        val status = Status.NOT_FOUND
    }
    object DatabaseError : RegistrationError() {
        val message = "A database error occurred."
        val code = ErrorCode.DATABASE_ERROR
        val status = Status.INTERNAL_SERVER_ERROR
    }
    object EventClosed : RegistrationError() {
        val message = "The event is closed."
        val code = ErrorCode.EVENT_CLOSED_OR_MISSING
        val status = Status.GONE
    }
    object ParticipantNotFound : RegistrationError() {
        val message = "The participant was not found with that token."
        val code = ErrorCode.NOT_FOUND
        val status = Status.NOT_FOUND
    }
}

fun RegistrationError.toResponse(): Response = when(this) {
    RegistrationError.EventNotFound ->
        with(RegistrationError.EventNotFound) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    RegistrationError.DatabaseError ->
        with(RegistrationError.DatabaseError) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    RegistrationError.EventClosed ->
        with(RegistrationError.EventClosed) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    RegistrationError.ParticipantNotFound ->
        with(RegistrationError.ParticipantNotFound) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
}
