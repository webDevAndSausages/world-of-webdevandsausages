package org.webdevandsausages.events.dto

import meta.enums.ParticipantStatus

data class ParticipantDto(
    val email: String,
    val name: String,
    val verificationToken: String,
    val status: ParticipantStatus,
    val affiliation: String? = null,
    val orderNumber: Int,
    val insertedOn: String? = null
)

val ParticipantDto.participantDetails get() = mapOf(
    "email" to email,
    "name" to name,
    "affiliation" to affiliation,
    "insertedOn" to insertedOn
    )