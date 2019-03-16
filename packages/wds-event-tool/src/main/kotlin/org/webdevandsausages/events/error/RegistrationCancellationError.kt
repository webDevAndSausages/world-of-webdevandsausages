package org.webdevandsausages.events.error

import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto

sealed class RegistrationCancellationError {
    object EventNotFound : RegistrationCancellationError() {
        val message = "The event is closed or non-existent."
        val code = ErrorCode.EVENT_CLOSED_OR_MISSING
        val status = Status.NOT_FOUND
    }
    object DatabaseError : RegistrationCancellationError() {
        val message = "A database error occurred."
        val code = ErrorCode.DATABASE_ERROR
        val status = Status.INTERNAL_SERVER_ERROR
    }
    object EventClosed : RegistrationCancellationError()  {
        val message = "The event is closed."
        val code = ErrorCode.NOT_FOUND
        val status = Status.NOT_FOUND
    }
    object ParticipantNotFound : RegistrationCancellationError() {
        val message = "A registration was not found with provided token."
        val code = ErrorCode.PARTICIPANT_NOT_FOUND
        val status = Status.NOT_FOUND
    }
    object ParticipantAlreadyCancelled: RegistrationCancellationError()  {
        val message = "This registration was already cancelled."
        val code = ErrorCode.ALREADY_CANCELLED
        val status = Status.UNPROCESSABLE_ENTITY
    }
    object ShouldNeverHappen: RegistrationCancellationError() {
        val message = "Something weird happened. Should never happen, lol"
        val code = ErrorCode.SHOULD_NEVER_HAPPEN
        val status = Status.INTERNAL_SERVER_ERROR
    }
}

fun RegistrationCancellationError.toResponse(): Response = when(this) {
    RegistrationCancellationError.EventNotFound ->
        with(RegistrationCancellationError.EventNotFound) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    RegistrationCancellationError.DatabaseError ->
        with(RegistrationCancellationError.DatabaseError) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    RegistrationCancellationError.EventClosed ->
        with(RegistrationCancellationError.EventClosed) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    RegistrationCancellationError.ParticipantNotFound ->
        with(RegistrationCancellationError.ParticipantNotFound) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    RegistrationCancellationError.ParticipantAlreadyCancelled ->
        with(RegistrationCancellationError.ParticipantAlreadyCancelled) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
    RegistrationCancellationError.ShouldNeverHappen ->
        with(RegistrationCancellationError.ShouldNeverHappen) {
            Router.errorResponseLens(ErrorOutDto(this.message, this.code), Response(this.status))
        }
}


