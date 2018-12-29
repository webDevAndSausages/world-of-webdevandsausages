package org.webdevandsausages.events.dao

import arrow.core.Option
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto

object ParticipantRepository {
    fun create(registration: RegistrationInDto): Option<ParticipantDto> = ParticipantCRUD.create(registration)

    fun findByToken(token: String): Option<ParticipantDto> = ParticipantCRUD.findByToken(token)

    fun findByIdOrLatest(id: Long? = null): Option<EventDto> = findByIdOrLatest(id)
}