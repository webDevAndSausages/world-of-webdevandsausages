package org.webdevandsausages.events.dao

import arrow.core.Option
import arrow.core.Try
import arrow.core.getOrDefault
import arrow.core.toOption
import meta.enums.ParticipantStatus
import meta.tables.Participant
import meta.tables.records.ParticipantRecord
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.utils.getFullName
import org.webdevandsausages.events.utils.prettified

class ParticipantCRUD(configuration: Configuration) {
    val db = DSL.using(configuration)

    val ParticipantRecord.fullName: String get() = getFullName(firstName, lastName)

    fun create(registration: RegistrationInDto, context: DSLContext = db): Option<ParticipantDto> {
        val (eventId, firstName, lastName, affiliation, email, verificationToken, orderNumber, status) = registration
        return with(Participant.PARTICIPANT) {
            context.use { ctx ->
                ctx
                    .insertInto(
                        Participant.PARTICIPANT,
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
                    .returning(
                        FIRST_NAME,
                        LAST_NAME,
                        EMAIL,
                        AFFILIATION,
                        VERIFICATION_TOKEN,
                        STATUS,
                        ORDER_NUMBER,
                        CREATED_ON
                    )
                    .fetchOne()
            }.let {
                ParticipantDto(
                    email = it.email,
                    name = it.fullName,
                    verificationToken = it.verificationToken,
                    affiliation = it.affiliation,
                    status = it.status,
                    orderNumber = it.orderNumber,
                    insertedOn = it.createdOn.prettified
                ).toOption()
            }
        }
    }

    fun findByToken(token: String, context: DSLContext = db): Option<ParticipantDto> {
        return Try {
            with(Participant.PARTICIPANT) {
                context.use { ctx ->
                    ctx
                        .selectFrom(this)
                        .where(this.VERIFICATION_TOKEN.eq(token))
                        .fetchOne()
                }?.let {
                    ParticipantDto(
                        email = it.email,
                        name = it.fullName,
                        verificationToken = it.verificationToken,
                        status = it.status,
                        orderNumber = it.orderNumber / 1000,
                        affiliation = it.affiliation
                    )
                }
            }
        }.getOrDefault { null }.toOption()
    }

    fun updateStatus(
        id: Long,
        status: ParticipantStatus,
        context: DSLContext = db
    ): Option<meta.tables.pojos.Participant> =
        context.update(Participant.PARTICIPANT).set(Participant.PARTICIPANT.STATUS, status).where(
            Participant
                .PARTICIPANT.ID.eq(id)
        ).returning().fetchOne().into(meta.tables.pojos.Participant::class.java).toOption()

}
