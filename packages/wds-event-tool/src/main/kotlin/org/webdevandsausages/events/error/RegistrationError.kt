package org.webdevandsausages.events.error

sealed class RegistrationError {
    object EventNotFound : RegistrationError()
    object DatabaseError : RegistrationError()
    object EventClosed : RegistrationError()
    object ParticipantNotFound : RegistrationError()
}
