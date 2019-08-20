package org.webdevandsausages.events.error

import org.http4k.core.Status
import org.webdevandsausages.events.dto.ErrorCode

abstract class WDSException(
    override val message: String = "A server error occurred",
    val status: Status,
    val code: ErrorCode
) : Throwable(message)
