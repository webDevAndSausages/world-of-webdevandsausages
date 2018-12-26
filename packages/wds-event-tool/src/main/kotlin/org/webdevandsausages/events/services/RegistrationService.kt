package org.webdevandsausages.events.services

import arrow.core.Option
import org.webdevandsausages.events.dao.ParticipantCRUD
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto

object RegistrationService {
    fun create(registration: RegistrationInDto): Option<ParticipantDto> {
        return ParticipantCRUD.create(registration)
    }

    fun getByToken(token: String): Option<ParticipantDto> {
        return ParticipantCRUD.findByToken(token)
    }
}