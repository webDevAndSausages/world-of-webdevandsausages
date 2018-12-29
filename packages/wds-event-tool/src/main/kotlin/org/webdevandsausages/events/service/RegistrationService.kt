package org.webdevandsausages.events.service

import org.slf4j.Logger
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.utils.prettified
import arrow.core.Option
import arrow.core.None
import arrow.core.Some
import arrow.core.Either
import arrow.core.Try
import arrow.core.getOrDefault
import arrow.core.right
import meta.enums.ParticipantStatus
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.ParticipantCRUD
import org.webdevandsausages.events.dto.getNextOrderNumber
import org.webdevandsausages.events.dto.getNextOrderNumberInStatusGroup
import org.webdevandsausages.events.dto.getPosition
import org.webdevandsausages.events.utils.RandomWordsUtil

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
    val eventCRUD: EventCRUD,
    val participantCRUD: ParticipantCRUD,
    val randomWordUtil: RandomWordsUtil,
    val emailService: EmailService,
    val logger: Logger
) : CreateRegistrationController {
    override fun invoke(registration: RegistrationInDto): Either<EventError, ParticipantDto?> {
        val eventData: Option<EventDto> = eventCRUD.findByIdOrLatest(registration.eventId)

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
                        val nextNumber = eventData.t.participants.getNextOrderNumber()
                        val registrationWithToken = registration.copy(registrationToken = token, orderNumber = nextNumber, status = status)
                        val result = participantCRUD.create(registrationWithToken)

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
                            is Some -> {
                                val resultWithReadableOrderNumber = result.t.copy(
                                    orderNumber = eventData.t.participants.getNextOrderNumberInStatusGroup(status)
                                )
                                Either.Right(resultWithReadableOrderNumber)
                            }
                            is None -> Either.Left(EventError.DatabaseError)
                        }
                    }
                }
        }
    }

    fun getVerificationToken(): String {
        var token: String?
        do {
            token = Try { randomWordUtil.getWordPair() }.getOrDefault { null }
        } while (token !is String || participantCRUD.findByToken(token).isDefined())
        return token
    }
}

interface GetRegistrationController {
    operator fun invoke(eventId: Long, verificationToken: String): Either<RegistrationError, ParticipantDto?>
}

class GetRegistrationControllerImpl(
    val eventCRUD: EventCRUD,
    val participantCRUD: ParticipantCRUD,
    val logger: Logger
) : GetRegistrationController {
    private fun getParticipant(token: String, event: EventDto): Either<RegistrationError, ParticipantDto?> {
        val participantData = participantCRUD.findByToken(token)
        return when (participantData) {
            is None -> Either.left(RegistrationError.ParticipantNotFound)
            is Some -> participantData.t.let {
                val position = event.participants.getPosition(it.status, it.verificationToken)
                // this shouldn't happen
                if (position == -1) {
                    logger.error("GET registration endpoint: Saved participant with token ${it.verificationToken} was not found from list of event participants.")
                    return Either.left(RegistrationError.ParticipantNotFound)
                }
                it.copy(orderNumber = position)
            }.right()
        }
    }

    override fun invoke(eventId: Long, verificationToken: String): Either<RegistrationError, ParticipantDto?> {
        val eventData = eventCRUD.findByIdOrLatest(eventId)
        return when (eventData) {
            is None -> Either.left(RegistrationError.EventNotFound)
            is Some -> when {
                eventData.t.event.status.isInvisible -> Either.left(RegistrationError.EventClosed)
                else -> getParticipant(verificationToken, eventData.t)
            }
        }
    }
}

val ParticipantStatus.toText get() = this.name.toLowerCase().replace("_", " ")
