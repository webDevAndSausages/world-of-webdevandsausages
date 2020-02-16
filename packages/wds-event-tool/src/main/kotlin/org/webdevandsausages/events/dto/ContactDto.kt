package org.webdevandsausages.events.dto

data class ContactDto(
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val unsubscribed: Boolean = false
)