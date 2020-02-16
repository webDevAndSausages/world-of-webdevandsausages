package org.webdevandsausages.events.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import org.webdevandsausages.events.dao.ContactCRUD
import org.webdevandsausages.events.domain.DomainError
import org.webdevandsausages.events.domain.DomainSuccess
import org.webdevandsausages.events.dto.ContactDto
import org.webdevandsausages.events.utils.createLogger


class CreateContactService(private val contactCRUD: ContactCRUD) {
    private val log = createLogger()

    operator fun invoke(contact: ContactDto): Result<DomainSuccess, DomainError> {
        log.info("adding contact to mailing list")

        return contactCRUD.create(contact).mapBoth(
            { Ok(DomainSuccess.Created) },
            { Err(DomainError.DatabaseError) }
        )
    }
}