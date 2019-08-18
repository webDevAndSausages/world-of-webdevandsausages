package org.webdevandsausages.events.dto

import meta.enums.ParticipantStatus

data class RegistrationInDto(
    var eventId: Long? = null,
    val firstName: String?,
    val lastName: String?,
    var affiliation: String? = "-",
    val email: String,
    var registrationToken: String = "-",
    var orderNumber: Int = 0,
    var status: ParticipantStatus = ParticipantStatus.REGISTERED,
    val subscribe: Boolean? = true
)