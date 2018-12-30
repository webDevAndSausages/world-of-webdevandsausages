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
import org.webdevandsausages.events.dao.EventRepository
import org.webdevandsausages.events.dao.ParticipantRepository
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

interface CreateRegistrationService {
    operator fun invoke(registration: RegistrationInDto): Either<EventError, ParticipantDto?>
}

class CreateRegistrationServiceImpl(
    val eventRepository: EventRepository,
    val participantRepository: ParticipantRepository,
    val randomWordsUtil: RandomWordsUtil,
    val emailService: EmailService,
    val firebaseService: FirebaseService,
    val logger: Logger
) : CreateRegistrationService {
    override fun invoke(registration: RegistrationInDto): Either<EventError, ParticipantDto?> {
        val eventData: Option<EventDto> = eventRepository.findByIdOrLatest(registration.eventId)

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
                        val result = participantRepository.create(registrationWithToken)

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

                            logger.info("Dispatching participant to firebase mailing list")
                            if (registration.subscribe != null && registration.subscribe) {
                                firebaseService.upsertParticipantToMailingList(result.t)
                            }
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
            token = Try { randomWordsUtil.getWordPair() }.getOrDefault { null }
        } while (token !is String || participantRepository.findByToken(token).isDefined())
        return token
    }
}

interface GetRegistrationService {
    operator fun invoke(eventId: Long, verificationToken: String): Either<RegistrationError, ParticipantDto?>
}

class GetRegistrationServiceImpl(
    val eventRepository: EventRepository,
    val participantRepository: ParticipantRepository,
    val logger: Logger
) : GetRegistrationService {
    private fun getParticipant(token: String, event: EventDto): Either<RegistrationError, ParticipantDto?> {
        val participantData = participantRepository.findByToken(token)
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
        val eventData = eventRepository.findByIdOrLatest(eventId)
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
