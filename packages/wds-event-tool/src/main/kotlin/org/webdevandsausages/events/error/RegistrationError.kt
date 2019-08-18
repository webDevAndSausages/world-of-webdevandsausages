package org.webdevandsausages.events.error

import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto

sealed class RegistrationError(override val message: String = "", val status: Status = Status.INTERNAL_SERVER_ERROR) :
    Throwable(message) {
    object EventNotFound : RegistrationError("The event does not exist.", Status.NOT_FOUND) {
        val code = ErrorCode.NOT_FOUND
    }

    object DatabaseError : RegistrationError("A database error occurred.", Status.INTERNAL_SERVER_ERROR) {
        val code = ErrorCode.DATABASE_ERROR
    }

    object EventClosed : RegistrationError("The event is closed.", Status.GONE) {
        val code = ErrorCode.EVENT_CLOSED_OR_MISSING
    }

    object ParticipantNotFound : RegistrationError("The participant was not found with that token.", Status.NOT_FOUND) {
        val code = ErrorCode.NOT_FOUND
    }
}

fun RegistrationError.toResponse(): Response = when (this) {
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
