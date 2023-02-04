package org.webdevandsausages.events.dao

import com.apurebase.kgraphql.context
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import meta.tables.Contact
import meta.tables.EmailBlacklist.EMAIL_BLACKLIST
import meta.tables.records.EmailBlacklistRecord
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.InsertOnDuplicateSetStep
import org.jooq.impl.DSL
import org.webdevandsausages.events.controller.Recipient
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

    fun findAllEmails(ctx: DSLContext = db): Result<List<String>, Throwable> {
        return runCatching {
            ctx.selectFrom(Contact.CONTACT).where(Contact.CONTACT.SUBSCRIBE.isTrue()).fetch(Contact.CONTACT.EMAIL)
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

    fun addEmailsToBlacklist(emails: List<String>, context: DSLContext = db): Result<List<Int>, Throwable> {
        return runCatching {
            emails.map{
                context.insertInto(EMAIL_BLACKLIST, EMAIL_BLACKLIST.EMAIL).values(it).onDuplicateKeyIgnore().execute()
            }
        }
    }

    fun unsubscribeEmail(email: String, context: DSLContext = db): Result<Int, Throwable> {
        return runCatching {
            context.update(Contact.CONTACT).set(Contact.CONTACT.SUBSCRIBE, false)
                .where(Contact.CONTACT.EMAIL.eq(email)).execute()
        }
    }
}
