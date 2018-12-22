package org.webdevandsausages.events.dao

import meta.enums.ParticipantStatus
import meta.tables.daos.EventDao
import meta.tables.Participant
import org.jooq.impl.DSL
import org.webdevandsausages.events.config.local
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto

object ParticipantCRUD: EventDao(local.jooqConfiguration) {

    val db = DSL.using(configuration())

    fun createParticipant(registration: RegistrationInDto): ParticipantDto? {
        val (eventId, firstName, lastName, affiliation, email, verificationToken, status) = registration
        val place = 1000
        val participant = with(Participant.PARTICIPANT) {
            db.use { ctx ->
                ctx
                    .insertInto(Participant.PARTICIPANT,
                        FIRST_NAME,
                        LAST_NAME,
                        EMAIL,
                        AFFILIATION,
                        VERIFICATION_TOKEN,
                        ORDER_NUMBER,
                        EVENT_ID,
                        STATUS
                    )
                    .values(
                        firstName,
                        lastName,
                        email,
                        affiliation,
                        verificationToken,
                        place,
                        eventId,
                        status
                   )
                    .execute()
            }
        }
        return if (participant == 1) ParticipantDto(email, verificationToken) else null
    }

}