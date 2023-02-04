package org.webdevandsausages.events.service

import arrow.fx.IODispatchers.CommonPool
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webdevandsausages.events.controller.BounceComplaintInDto
import org.webdevandsausages.events.dao.ContactCRUD
import org.webdevandsausages.events.domain.DomainError
import org.webdevandsausages.events.domain.DomainSuccess
import org.webdevandsausages.events.dto.ContactDto
import org.webdevandsausages.events.utils.createLogger


class CreateContactService(private val contactCRUD: ContactCRUD, private val emailService: EmailService) : CoroutineScope by CoroutineScope(
    Dispatchers.Default) {
    private val log = createLogger()

    operator fun invoke(contact: ContactDto): Result<DomainSuccess, DomainError> {
        log.info("adding contact to mailing list")

        return contactCRUD.create(contact).mapBoth(
            { Ok(DomainSuccess.Created) },
            { Err(DomainError.DatabaseError) }
        ).onSuccess {
            sendEmail(contact)
        }
    }

    fun sendEmail(contact: ContactDto) = launch(CommonPool) {
        emailService.sendMailingListJoinConfirmation(contact)
    }


}

class GetContactEmailsService(private val contactCRUD: ContactCRUD) : CoroutineScope by CoroutineScope(
    Dispatchers.Default) {
    private val log = createLogger()

    operator fun invoke(): Result<List<String>, DomainError> {
        log.info("adding contact to mailing list")

        return contactCRUD.findAllEmails().mapBoth(
            { Ok(it) },
            { Err(DomainError.DatabaseError) }
        )
    }
}

class CreateBlacklistService(private val contactCRUD: ContactCRUD) : CoroutineScope by CoroutineScope(
    Dispatchers.Default) {
    private val log = createLogger()

    operator fun invoke(bounceOrComplaint: BounceComplaintInDto): Result<DomainSuccess, DomainError> {
        log.info("Got a ${bounceOrComplaint.eventType} notification")
        log.info("Adding emails to blacklist")
        return contactCRUD.addEmailsToBlacklist(bounceOrComplaint.info.recipients.map { it.emailAddress }).mapBoth(
            { Ok(DomainSuccess.Created) },
            { Err(DomainError.DatabaseError) }
        )
    }
}

class UnsubscribeEmailService(private val contactCRUD: ContactCRUD) : CoroutineScope by CoroutineScope(
    Dispatchers.Default) {
    private val log = createLogger()

    operator fun invoke(email: String): Result<DomainSuccess, DomainError> {
        log.info("Unsubscribing email ${email}")
        return contactCRUD.unsubscribeEmail(email).mapBoth(
            { Ok(DomainSuccess.Updated) },
            { Err(DomainError.DatabaseError) }
        )
    }
}
