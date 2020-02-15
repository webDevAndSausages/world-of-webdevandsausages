package org.webdevandsausages.events.dto

import meta.enums.ParticipantStatus
import meta.tables.pojos.Participant
import org.webdevandsausages.events.utils.getFullName
import org.webdevandsausages.events.utils.prettified

data class ParticipantDto(
    val email: String,
    val name: String,
    val verificationToken: String,
    val status: ParticipantStatus,
    val affiliation: String? = null,
    val orderNumber: Int,
    val insertedOn: String? = null
) {
    constructor(participant: Participant?) : this(
        participant!!.email,
        getFullName(participant),
        participant.verificationToken,
        participant.status,
        participant.affiliation,
        participant.orderNumber,
        participant.createdOn.prettified
    )
}

val ParticipantDto.participantDetails
    get() = mapOf(
        "email" to email,
        "name" to name,
        "affiliation" to affiliation,
        "insertedOn" to insertedOn
    )