package org.webdevandsausages.events.dao

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import meta.tables.Contact
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.webdevandsausages.events.dto.ContactDto

class ContactCRUD(configuration: Configuration) {
    private val db = DSL.using(configuration)
    private val c = Contact.CONTACT

    fun create(contact: ContactDto, ctx: DSLContext = db): Result<Int, Throwable> {
        return runCatching {
            ctx.insertInto(c)
                .columns(c.EMAIL, c.FIRST_NAME, c.LAST_NAME, c.PHONE, c.SUBSCRIBE)
                .values(contact.email, contact.firstName, contact.lastName, contact.phone, contact.subscribe)
                .onConflictDoNothing()
                .execute()

        }
    }
}