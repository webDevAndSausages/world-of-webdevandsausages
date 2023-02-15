package org.webdevandsausages.events.dao

import arrow.core.Option
import arrow.core.toOption
import com.github.michaelbull.result.get
import com.github.michaelbull.result.runCatching
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
        return runCatching {
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
        }.get().toOption()
    }

    fun findEventParticipantEmails(
        eventId: Long,
        ctx: DSLContext = db
    ): com.github.michaelbull.result.Result<List<String>, Throwable> {
        return runCatching {
            ctx.selectFrom(Participant.PARTICIPANT).where(Participant.PARTICIPANT.EVENT_ID.eq(eventId))
                .and(Participant.PARTICIPANT.STATUS.ne(ParticipantStatus.CANCELLED))
                .fetch(Participant.PARTICIPANT.EMAIL)
        }
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
