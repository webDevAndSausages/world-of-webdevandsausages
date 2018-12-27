package org.webdevandsausages.events.controllers

import org.slf4j.Logger
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.services.EmailService
import org.webdevandsausages.events.services.EventService
import org.webdevandsausages.events.services.RandomTokenService
import org.webdevandsausages.events.services.RegistrationService
import org.webdevandsausages.events.utils.prettified
import arrow.core.Option
import arrow.core.None
import arrow.core.Some
import arrow.core.Either
import arrow.core.Try
import arrow.core.getOrDefault
import arrow.core.right
import meta.enums.ParticipantStatus
import org.webdevandsausages.events.dto.getPosition
import org.webdevandsausages.events.services.canRegister
import org.webdevandsausages.events.services.isInvisible
import org.webdevandsausages.events.services.toText

sealed class RegistrationError {
    object EventNotFound : RegistrationError()
    object DatabaseError : RegistrationError()
    object EventClosed : RegistrationError()
    object ParticipantNotFound : RegistrationError()
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

        return when (eventData) {
            is None -> Either.left(EventError.NotFound)
            is Some -> when {
                    !eventData.t.event.status.canRegister -> Either.left(EventError.NotFound)
                    eventData.t.participants.find { it.email == registration.email } != null -> Either.left(EventError.AlreadyRegistered)
                    else -> {
                        val event = eventData.t.event
                        val numRegistered = eventData.t.participants.size
                        // postgres trigger should flip status to full when limit is hit
                        val status = if (numRegistered < event.maxParticipants) ParticipantStatus.REGISTERED else ParticipantStatus.WAIT_LISTED
                        val token = getVerificationToken()
                        val lastNumber = eventData.t.participants.filter { it.status == status }.maxBy { it.orderNumber }?.orderNumber ?: 0
                        val registrationWithToken = registration.copy(registrationToken = token, orderNumber = lastNumber + 1000, status = status)
                        val result = registrationService.create(registrationWithToken)

                        if (result is Some) {
                            val sponsor = if (event.sponsor != null) event.sponsor else "Anonymous"
                            val emailData = mapOf(
                                "action" to status.toText,
                                "datetime" to event.date.prettified,
                                "location" to event.location,
                                "token" to result.t.verificationToken,
                                "sponsor" to sponsor
                                )

                            logger.info("Dispatching registration email to ${result.t.email}")
                            emailService.sendMail(
                                result.t.email,
                                result.t.name,
                                "Web Dev & Sausages Registration",
                                "d-91e5bf696190444d94f13e564fee4426",
                                emailData
                            )
                        }
                        return when (result) {
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
            token = Try { randomTokenService.getWordPair() }.getOrDefault { null }
        } while (token !is String || registrationService.getByToken(token).isDefined())
        return token
    }
}

interface GetRegistrationController {
    operator fun invoke(eventId: Long, verificationToken: String): Either<RegistrationError, ParticipantDto?>
}

class GetRegistrationControllerImpl(
    val eventService: EventService,
    val registrationService: RegistrationService
) : GetRegistrationController {
    private fun getParticipant(token: String, event: EventDto): Either<RegistrationError, ParticipantDto?> {
        val participantData = registrationService.getByToken(token)
        return when (participantData) {
            is None -> Either.left(RegistrationError.ParticipantNotFound)
            is Some -> participantData.t?.let {
                val position = event.participants.getPosition(it.status, it.verificationToken)
                it.copy(orderNumber = position)
            }.right()
        }
    }

    override fun invoke(eventId: Long, verificationToken: String): Either<RegistrationError, ParticipantDto?> {
        val eventData = eventService.getByIdOrLatest(eventId)
        return when (eventData) {
            is None -> return Either.left(RegistrationError.EventNotFound)
            is Some -> when {
                eventData.t.event.status.isInvisible -> return Either.left(RegistrationError.EventClosed)
                else -> getParticipant(verificationToken, eventData.t)
            }
        }
    }
}