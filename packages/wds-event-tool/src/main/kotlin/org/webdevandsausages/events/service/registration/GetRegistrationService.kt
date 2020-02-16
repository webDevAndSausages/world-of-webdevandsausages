package org.webdevandsausages.events.service.registration

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import arrow.core.right
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.ParticipantCRUD
import org.webdevandsausages.events.domain.RegistrationError
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.getPosition
import org.webdevandsausages.events.service.event.isInvisible
import org.webdevandsausages.events.utils.createLogger


class GetRegistrationService(
    val eventCRUD: EventCRUD,
    val participantCRUD: ParticipantCRUD
) {

    val logger = createLogger()

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

    fun getParticipant(token: String): Either<RegistrationError, ParticipantDto?> {
        val participantData = participantCRUD.findByToken(token)
        return when (participantData) {
            is None -> Either.left(RegistrationError.ParticipantNotFound)
            is Some -> participantData.t.right()
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