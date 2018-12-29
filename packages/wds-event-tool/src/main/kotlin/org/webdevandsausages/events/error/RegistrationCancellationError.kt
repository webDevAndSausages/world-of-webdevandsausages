package org.webdevandsausages.events.error

sealed class RegistrationCancellationError {
    object EventNotFound : RegistrationCancellationError()
    object DatabaseError : RegistrationCancellationError()
    object EventClosed : RegistrationCancellationError()
    object ParticipantNotFound : RegistrationCancellationError()
    object ParticipantAlreadyCancelled: RegistrationCancellationError()
    object ShouldNeverHappen: RegistrationCancellationError()
}
