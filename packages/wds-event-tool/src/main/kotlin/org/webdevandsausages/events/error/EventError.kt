package org.webdevandsausages.events.error

sealed class EventError {
    object NotFound : EventError()
    object AlreadyRegistered : EventError()
    object DatabaseError : EventError()
}
