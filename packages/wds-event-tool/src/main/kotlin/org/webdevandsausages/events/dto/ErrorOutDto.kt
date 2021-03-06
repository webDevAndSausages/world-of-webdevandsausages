package org.webdevandsausages.events.dto

enum class ErrorCode {
    NOT_FOUND,
    EVENT_CLOSED_OR_MISSING,
    ALREADY_REGISTERED,
    DATABASE_ERROR,
    INVALID_EMAIL,
    SHOULD_NEVER_HAPPEN,
    ALREADY_CANCELLED,
    PARTICIPANT_NOT_FOUND,
    VALIDATION_ERROR
}

data class ErrorOutDto(val message: String, val code: ErrorCode)
