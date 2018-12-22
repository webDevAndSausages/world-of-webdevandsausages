package org.webdevandsausages.events.controllers

import org.slf4j.Logger
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.services.RandomTokenService
import org.webdevandsausages.events.services.RegistrationService

interface CreateRegistrationController {
    operator fun invoke(registration: RegistrationInDto): ParticipantDto?
}

class CreateRegistrationControllerImpl(
    val registrationService: RegistrationService,
    val randomTokenService: RandomTokenService,
    val logger: Logger
  ) : CreateRegistrationController {
    override fun invoke(registration: RegistrationInDto): ParticipantDto? {
        /* TODO:
            - Check if event exists, open and status
            - Check if user already registered
            - Check if token already used
        */
        val token = randomTokenService.getWordPair()
        val registrationWithToken = registration.copy(registrationToken = token)
        return registrationService.createRegistration(registrationWithToken)
    }
}