package org.webdevandsausages.events.models

import java.util.Date

data class Registration(
    val email: String,
    val verificationToken: String
)

data class Event(
    val id: String?,
    val contact: String? = "No contact yet",
    val details: String? = "No details yet",
    val datetime: Date? = null,
    val location: String? = "Location not specified",
    val registered: List<Map<String, String>>? = emptyList(),
    val waitListed: List<Map<String, String>>? = emptyList(),
    val maxParticipants: Long? = 0,
    val volume: Long? = null,
    val sponsor: String?,
    val sponsorWWWLink: String? = "No sponser www link yet",
    val registrationOpens: Date? = null,
    val registrationCloses: Date? = null,
    val feedback: List<String>? = emptyList(),
    val feedbackAnswer: String? = "",
    val feedbackQuestion: String? = ""
)