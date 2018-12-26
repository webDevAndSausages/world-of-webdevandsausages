package org.webdevandsausages.events.dto

import meta.enums.ParticipantStatus

data class ParticipantDto(
    val email: String,
    val name: String,
    val verificationToken: String,
    val status: ParticipantStatus,
    val orderNumber: Int
)