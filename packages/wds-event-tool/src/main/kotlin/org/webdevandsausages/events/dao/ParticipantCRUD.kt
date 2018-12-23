package org.webdevandsausages.events.dao

import meta.enums.ParticipantStatus
import meta.tables.daos.EventDao
import meta.tables.Participant
import meta.tables.records.ParticipantRecord
import org.jooq.impl.DSL
import org.webdevandsausages.events.config.local
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.dto.RegistrationOutDto

object ParticipantCRUD: EventDao(local.jooqConfiguration) {

    fun db() = DSL.using(configuration())

    fun create(registration: RegistrationInDto): ParticipantDto? {
        val (eventId, firstName, lastName, affiliation, email, verificationToken, orderNumber, status) = registration
        return with(Participant.PARTICIPANT) {
            db().use { ctx ->
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
                        orderNumber,
                        eventId,
                        status
                   )
                    .returning(EMAIL, VERIFICATION_TOKEN, STATUS, ORDER_NUMBER)
                    .fetchOne()
            }?.let {
                ParticipantDto(
                    email = it.email,
                    name = "${it.firstName ?: "-" } ${it.lastName ?: ""}".trim(),
                    verificationToken = it.verificationToken,
                    status = it.status,
                    // usually the number should be a multiple of 1000, but if a
                    // participant was inserted between, we round them to a reasonable order number
                    orderNumber = Math.round((it.orderNumber / 1000).toDouble()).toInt()
                  )
            }
        }
    }

    fun findByToken(token: String): ParticipantDto? {
        return with(Participant.PARTICIPANT) {
            db().use { ctx ->
                ctx
                    .selectFrom(this)
                    .where(this.VERIFICATION_TOKEN.eq(token))
                    .fetchOne()
            }?.let {
                ParticipantDto(
                    email = it.email,
                    name = "${it.firstName ?: "-" } ${it.lastName ?: ""}".trim(),
                    verificationToken = it.verificationToken,
                    status = it.status,
                    orderNumber = it.orderNumber / 1000
                              )
            }
        }
    }

}