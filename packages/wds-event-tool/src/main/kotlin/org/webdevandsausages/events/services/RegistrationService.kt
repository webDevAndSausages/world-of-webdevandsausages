package org.webdevandsausages.events.services

import org.webdevandsausages.events.dao.ParticipantCRUD
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto

object RegistrationService {
    fun createRegistration(registration: RegistrationInDto): ParticipantDto? {
        return ParticipantCRUD.createParticipant(registration)
    }
}