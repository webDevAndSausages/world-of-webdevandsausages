package org.webdevandsausages.events.service

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Try
import arrow.core.getOrDefault
import arrow.core.right
import arrow.core.toOption
import meta.enums.EventStatus
import meta.enums.ParticipantStatus
import meta.tables.pojos.Participant
import org.slf4j.Logger
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.ParticipantCRUD
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.dto.getNextOrderNumber
import org.webdevandsausages.events.dto.getNextOrderNumberInStatusGroup
import org.webdevandsausages.events.dto.getPosition
import org.webdevandsausages.events.error.EventError
import org.webdevandsausages.events.error.RegistrationCancellationError
import org.webdevandsausages.events.error.RegistrationError
import org.webdevandsausages.events.utils.RandomWordsUtil
import org.webdevandsausages.events.utils.prettified

class CreateRegistrationService(
    val eventCRUD: EventCRUD,
    val participantCRUD: ParticipantCRUD,
    val randomWordsUtil: RandomWordsUtil,
    val emailService: EmailService,
    val firebaseService: FirebaseService,
    val logger: Logger
) {
    operator fun invoke(registration: RegistrationInDto): Either<EventError, ParticipantDto?> {
        val eventData: Option<EventDto> = eventCRUD.findByIdOrLatest(registration.eventId)

        return when (eventData) {
            is None -> Either.left(EventError.NotFound)
            is Some -> when {
                    !eventData.t.event.status.canRegister -> Either.left(EventError.NotFound)
                    eventData.t.participants.find { it.email == registration.email && it.status != ParticipantStatus.CANCELLED } != null -> Either.left(EventError.AlreadyRegistered)
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
        } while (token !is String || participantCRUD.findByToken(token).isDefined())
        return token
    }
}

class GetRegistrationService(
    val eventCRUD: EventCRUD,
    val participantCRUD: ParticipantCRUD,
    val logger: Logger
) {
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

    operator fun invoke(eventId: Long, verificationToken: String): Either<RegistrationError, ParticipantDto?> {
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

class CancelRegistrationService(val eventCRUD: EventCRUD, val participantCRUD: ParticipantCRUD, val emailService: EmailService) {
    operator fun invoke(token: String): Either<RegistrationCancellationError, ParticipantDto> {
        val event = eventCRUD.findByParticipantToken(token)

        return when (event) {
            is Some -> {
                val participant =
                    event.t.participants.find { it.verificationToken == token }
                when (event.t.event.status) {
                    // Valid statuses for cancellation
                    EventStatus.OPEN_WITH_WAITLIST, EventStatus.OPEN_FULL, EventStatus.OPEN ->
                        when (participant?.status) {
                            ParticipantStatus.ORGANIZER, ParticipantStatus.WAIT_LISTED, ParticipantStatus.REGISTERED ->
                                updateStatusToCancelled(
                                    participant,
                                    event
                                )
                            ParticipantStatus.CANCELLED -> Either.left(RegistrationCancellationError.ParticipantAlreadyCancelled)
                            null -> Either.left(RegistrationCancellationError.ShouldNeverHappen) // Should never be null because event was found by participant token
                        }
                    else -> Either.left(
                        RegistrationCancellationError.EventClosed
                    )
                }
            }
            is None -> Either.left(RegistrationCancellationError.EventNotFound)
        }
    }


    private fun updateStatusToCancelled(
        participant: Participant,
        event: Some<EventDto>
    ): Either<RegistrationCancellationError, ParticipantDto> {
        val updatedParticipant = participantCRUD.updateStatus(participant.id, ParticipantStatus.CANCELLED)

        return when (updatedParticipant) {
            is Some -> {
                val registeredParticipants =
                    event.t.participants.filter { it.id != updatedParticipant.t.id && it.status == ParticipantStatus.REGISTERED }
                val hasRoomForNextOnWaitList = registeredParticipants.size < event.t.event.maxParticipants
                val waitListedParticipants =
                    event.t.participants.filter { it.id != updatedParticipant.t.id && it.status == ParticipantStatus.WAIT_LISTED }
                // Give a spot to next on waiting list if there's now room for new participants
                if (waitListedParticipants.isNotEmpty() && hasRoomForNextOnWaitList) {
                    // if there are wait listed participants, change status and move next to registered from wait list
                    val nextOnWaitingList = waitListedParticipants.minBy { it.orderNumber }.toOption()
                    when (nextOnWaitingList) {
                        is Some -> {
                            participantCRUD.updateStatus(
                                nextOnWaitingList.t.id,
                                ParticipantStatus.REGISTERED
                            )
                            // TODO: Send cancel confirmation email
                            // TODO: Send email to lucky one who got out of waiting list
                            Either.right(ParticipantDto(updatedParticipant.t))
                        }
                        is None ->
                            // We already checked that we have wait listed participants, so this should never happen
                            Either.left(RegistrationCancellationError.ShouldNeverHappen)

                    }
                } else {
                    // TODO: Send cancel confirmation email
                    Either.right(ParticipantDto(updatedParticipant.t))
                }
            }
            is None -> Either.left(RegistrationCancellationError.DatabaseError)
        }
    }
}

val ParticipantStatus.toText get() = this.name.toLowerCase().replace("_", " ")
