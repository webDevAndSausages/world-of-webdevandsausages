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

class ParticipantCRUD(configuration: Configuration) {
    val db = DSL.using(configuration)

    val ParticipantRecord.fullName: String get() = getFullName(firstName, lastName)

    fun create(registration: RegistrationInDto, context: DSLContext = db): Option<ParticipantDto> = context.use { ctx ->
        val participantRecord = ctx.newRecord(Participant.PARTICIPANT)
        participantRecord.from(registration)
        participantRecord.setVerificationToken(registration.registrationToken)
        participantRecord.store()
        participantRecord.refresh()
        ParticipantDto(participantRecord.into(meta.tables.pojos.Participant::class.java)).toOption()
    }

    fun findByToken(token: String, context: DSLContext = db): Option<ParticipantDto> {
        return Try {
            with(Participant.PARTICIPANT) {
                context.use { ctx ->
                    ctx
                        .selectFrom(this)
                        .where(VERIFICATION_TOKEN.eq(token))
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
