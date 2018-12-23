package org.webdevandsausages.events.dto

enum class ErrorCode {
    NOT_FOUND,
    EVENT_CLOSED_OR_MISSING,
    ALREADY_REGISTERED
}

data class ErrorOutDto(val message: String, val code: ErrorCode)