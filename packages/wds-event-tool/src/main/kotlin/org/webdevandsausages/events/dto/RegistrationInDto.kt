package org.webdevandsausages.events.dto

import meta.enums.ParticipantStatus

data class RegistrationInDto(
    var eventId: Long? = null,
    val firstName: String?,
    val lastName: String?,
    val affiliation: String?,
    val email: String,
    val registrationToken: String = "-",
    val orderNumber: Int = 0,
    val status: ParticipantStatus = ParticipantStatus.REGISTERED
)