package org.webdevandsausages.events.controllers

import org.slf4j.Logger
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.services.EmailService
import org.webdevandsausages.events.services.EventService
import org.webdevandsausages.events.services.RandomTokenService
import org.webdevandsausages.events.services.RegistrationService
import org.webdevandsausages.events.services.isOpenRegistrationStatus
import org.webdevandsausages.events.utils.prettified
import arrow.core.*

sealed class EventError {
    object NotFound : EventError()
    object AlreadyRegistered : EventError()
    object DatabaseError : EventError()
}


interface CreateRegistrationController {
    operator fun invoke(registration: RegistrationInDto): Either<EventError, ParticipantDto?>
}

class CreateRegistrationControllerImpl(
    val eventService: EventService,
    val registrationService: RegistrationService,
    val randomTokenService: RandomTokenService,
    val emailService: EmailService,
    val logger: Logger
  ) : CreateRegistrationController {
    override fun invoke(registration: RegistrationInDto): Either<EventError, ParticipantDto?> {
        val eventData: Option<EventDto> = eventService.getByIdOrLatest(registration.eventId)

        return when(eventData) {
            is None -> Either.left(EventError.NotFound)
            is Some -> when {
                    !eventData.t.event.status.isOpenRegistrationStatus -> Either.left(EventError.NotFound)
                    eventData.t.participants.find { it.email == registration.email} != null -> Either.left(EventError.AlreadyRegistered)
                    else -> {
                        val event = eventData.t.event
                        val token = getVerificationToken()
                        val lastNumber = eventData.t.participants.maxBy { it.orderNumber }?.orderNumber ?: 0
                        val registrationWithToken = registration.copy(registrationToken = token, orderNumber = lastNumber + 1000)
                        val result = registrationService.create(registrationWithToken)

                        if (result is Some) {
                            val sponsor = if (event.sponsor != null) event.sponsor else "Anonymous"
                            val emailData = mapOf(
                                "action" to "registered",
                                "datetime" to event.date.prettified,
                                "location" to event.location,
                                "token" to result.t.verificationToken,
                                "sponsor" to sponsor
                                                 )

                            emailService.sendMail(
                                result.t.email,
                                result.t.name,
                                "Web Dev & Sausages Registration",
                                "d-91e5bf696190444d94f13e564fee4426",
                                emailData
                                                 )
                        }
                        return when(result) {
                            is Some -> Either.Right(result.t)
                            is None -> Either.Left(EventError.DatabaseError)
                        }
                    }
                }
        }
    }

    fun getVerificationToken(): String {
        var token: String?
        do {
            token = randomTokenService.getWordPair()
        } while(token !is String || registrationService.getByToken(token) != null)
        return token
    }
}