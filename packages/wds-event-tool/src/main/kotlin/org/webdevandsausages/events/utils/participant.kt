package org.webdevandsausages.events.utils

import meta.tables.pojos.Participant

fun getFullName(firstName: String?, lastName: String?) = "${firstName ?: "-"} ${lastName ?: ""}".trim()
fun getFullName(participant: Participant) = getFullName(participant.firstName, participant.lastName)

