package org.webdevandsausages.events.domain

import org.http4k.core.Status

sealed class DomainSuccess(val status: Status = Status.OK) {
    object Created : DomainSuccess(Status.CREATED)
    object Updated : DomainSuccess(Status.OK)
    object Deleted : DomainSuccess(Status.OK)
}