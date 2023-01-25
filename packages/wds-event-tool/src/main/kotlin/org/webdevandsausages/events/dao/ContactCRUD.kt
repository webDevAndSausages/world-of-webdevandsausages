package org.webdevandsausages.events.dao

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.runCatching
import meta.enums.EventStatus
import meta.tables.Contact
import meta.tables.EmailBlacklist
import meta.tables.EmailBlacklist.EMAIL_BLACKLIST
import meta.tables.pojos.Event
import meta.tables.pojos.Participant
import meta.tables.records.EmailBlacklistRecord
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.simpleflatmapper.util.TypeReference
import org.webdevandsausages.events.dto.ContactDto
import org.webdevandsausages.events.dto.EventDto
import kotlin.streams.toList

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

    fun isEmailBlacklisted(email: String, context: DSLContext = db): Boolean {
        val record = context.use { ctx ->
            ctx.selectFrom(EMAIL_BLACKLIST)
                .where(EMAIL_BLACKLIST.EMAIL.eq(email))
                .fetchOne()
        }

        return record != null
    }
}
