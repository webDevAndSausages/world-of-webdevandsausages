package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.michaelbull.result.fold
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.urlEncoded
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.service.EmailService
import org.webdevandsausages.events.service.GetEventParticipantEmailsService
import org.webdevandsausages.events.service.GetMailingListContactEmailsService
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.webdevandsausages.events.utils.encrypt

data class SpamInDto(val template: String, val subject: String, val eventId: Long?)

object PostSpam : ApiRouteWithGraphqlConfig {
    private var emailService: EmailService? = null
    private var mailingListContacts: GetMailingListContactEmailsService? = null
    private var eventParticipants: GetEventParticipantEmailsService? = null
    private val SpamLens = Body.auto<SpamInDto>().toLens()

    operator fun invoke(
        emailService: EmailService,
        contacts: GetMailingListContactEmailsService,
        eventParticipants: GetEventParticipantEmailsService
    ): PostSpam {
        this.emailService = emailService
        this.mailingListContacts = contacts
        this.eventParticipants = eventParticipants
        return this
    }

    private fun handleSpamming(): HttpHandler = { req: Request ->
        SpamLens(req).let { (template, subject, eventId) ->
            when {
                eventId == null -> this.mailingListContacts!!.invoke()
                else -> this.eventParticipants!!.invoke(eventId)
            }.fold({
                it.forEach { email ->
                    runBlocking {
                        delay(100)
                        encrypt(email, System.getenv("PUBLIC_WDS_API_KEY")).map { hashedEmail ->
                            emailService!!.sendMail(
                                email, subject, template, mapOf(
                                    "unsubscribe_link" to "http://webdevandsausages.org/api/1.0/unsubscribe?hash=${hashedEmail.urlEncoded()}"
                                )
                            )
                        }


                    }
                }
                Response(Status.OK)
            }, {
                Response(Status.INTERNAL_SERVER_ERROR)
            })
        }

    }

    override val route: ContractRoute = "/spam" meta {
        summary = "Send message to all recipients on mailing list"
        returning(Status.OK to "Spam complete")
        returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
    } bindContract Method.POST to handleSpamming()

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        // TODO
    }

}
