package org.webdevandsausages.events.controller

import arrow.core.rightIfNotNull
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.michaelbull.result.fold
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.service.EmailService
import org.webdevandsausages.events.service.GetContactEmailsService
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.webdevandsausages.events.utils.encrypt

data class SpamInDto(val template: String, val subject: String)

object PostSpam : ApiRouteWithGraphqlConfig {
    private var emailService: EmailService? = null
    private var contacts: GetContactEmailsService? = null
    private val SpamLens = Body.auto<SpamInDto>().toLens()

    operator fun invoke(emailService: EmailService, contacts: GetContactEmailsService): PostSpam {
        this.emailService = emailService
        this.contacts = contacts
        return this
    }

    private fun handleSpamming(): HttpHandler = { req: Request ->
        SpamLens(req).let { (template, subject) ->
            this.contacts!!.invoke().fold({
                it.forEach { email ->
                    runBlocking {
                        delay(100)
                        encrypt(email, System.getenv("PUBLIC_WDS_API_KEY")).map { hashedEmail ->
                            emailService!!.sendMail(email, subject, template, mapOf(
                                "unsubscribe_link" to "http://webdevandsausages.org/api/1.0/unsubscribe?hash=${hashedEmail}"
                            ))
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
