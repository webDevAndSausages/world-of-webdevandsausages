package org.webdevandsausages.events.domain

import org.http4k.core.Status
import org.webdevandsausages.events.dto.ErrorCode


sealed class DomainError(
    message: String,
    status: Status = Status.INTERNAL_SERVER_ERROR,
    code: ErrorCode = ErrorCode.SHOULD_NEVER_HAPPEN
) : WDSException(message, status, code) {

    object DatabaseError : DomainError(
        "A database error occurred.",
        Status.INTERNAL_SERVER_ERROR,
        ErrorCode.DATABASE_ERROR
    )

    class ValidationError(message: String) : DomainError(message, Status.UNPROCESSABLE_ENTITY, ErrorCode.VALIDATION_ERROR)
}