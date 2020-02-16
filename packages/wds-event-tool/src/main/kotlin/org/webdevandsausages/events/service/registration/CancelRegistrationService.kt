package org.webdevandsausages.events.service.registration

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import arrow.core.toOption
import arrow.fx.IODispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import meta.enums.EventStatus
import meta.enums.ParticipantStatus
import meta.tables.pojos.Participant
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.ParticipantCRUD
import org.webdevandsausages.events.domain.RegistrationCancellationError
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.service.EmailService


class CancelRegistrationService(
    val eventCRUD: EventCRUD,
    val participantCRUD: ParticipantCRUD,
    val emailService: EmailService
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {
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
                            val participantRegisteredFromWaitList = participantCRUD.updateStatus(
                                nextOnWaitingList.t.id,
                                ParticipantStatus.REGISTERED
                            )

                            launch(IODispatchers.CommonPool) {
                                emailService.sendCancelConfirmationEmail(event.t, ParticipantDto(updatedParticipant.t))
                            }

                            when (participantRegisteredFromWaitList) {
                                is Some -> {
                                    launch {
                                        emailService.sendRegistrationEmailForWaitListed(
                                            event.t,
                                            ParticipantDto(
                                                participantRegisteredFromWaitList.t
                                            )
                                        )
                                    }

                                    Either.right(ParticipantDto(updatedParticipant.t))
                                }
                                is None -> {
                                    // Probably some database error? Very unlikely to happen
                                    Either.left(RegistrationCancellationError.DatabaseError)
                                }
                            }
                        }
                        is None ->
                            // We already checked that we have wait listed participants, so this should never happen
                            Either.left(RegistrationCancellationError.ShouldNeverHappen)

                    }
                } else {
                    launch(IODispatchers.CommonPool) {
                        emailService.sendCancelConfirmationEmail(event.t, ParticipantDto(updatedParticipant.t))
                    }

                    Either.right(ParticipantDto(updatedParticipant.t))
                }
            }
            is None -> Either.left(RegistrationCancellationError.DatabaseError)
        }
    }
}

val ParticipantStatus.toText get() = this.name.toLowerCase().replace("_", " ")
