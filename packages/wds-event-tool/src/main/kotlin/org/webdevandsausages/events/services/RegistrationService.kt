package org.webdevandsausages.events.services

import org.webdevandsausages.events.dao.ParticipantCRUD
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto

object RegistrationService {
    fun create(registration: RegistrationInDto): ParticipantDto? {
        return ParticipantCRUD.create(registration)
    }
    fun getByToken(token: String): ParticipantDto? {
        return ParticipantCRUD.findByToken(token)
    }
}