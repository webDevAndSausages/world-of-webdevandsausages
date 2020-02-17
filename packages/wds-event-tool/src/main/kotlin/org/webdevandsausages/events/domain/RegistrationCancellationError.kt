package org.webdevandsausages.events.domain

import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto

sealed class RegistrationCancellationError(
    message: String,
    status: Status = Status.INTERNAL_SERVER_ERROR,
    code: ErrorCode = ErrorCode.SHOULD_NEVER_HAPPEN
) : WDSException(message, status, code) {
    object EventNotFound : RegistrationCancellationError(
        "The event is closed or non-existent.",
        Status.NOT_FOUND,
        ErrorCode.EVENT_CLOSED_OR_MISSING
    )

    object DatabaseError : RegistrationCancellationError(
        "A database error occurred.",
        Status.INTERNAL_SERVER_ERROR,
        ErrorCode.DATABASE_ERROR
    )

    object EventClosed : RegistrationCancellationError("The event is closed.", Status.NOT_FOUND, ErrorCode.NOT_FOUND)

    object ParticipantNotFound :
        RegistrationCancellationError(
            "A registration was not found with provided token.",
            Status.NOT_FOUND,
            ErrorCode.PARTICIPANT_NOT_FOUND
        )

    object ParticipantAlreadyCancelled :
        RegistrationCancellationError(
            "This registration was already cancelled.",
            Status.UNPROCESSABLE_ENTITY,
            ErrorCode.ALREADY_CANCELLED
        )


    object ShouldNeverHappen : RegistrationCancellationError(
        "Something weird happened. Should never happen, lol"
    )
}

fun RegistrationCancellationError.toResponse(): Response = when (this) {
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


