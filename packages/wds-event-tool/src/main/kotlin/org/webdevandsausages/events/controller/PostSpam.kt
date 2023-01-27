package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.michaelbull.result.fold
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.service.EmailService
import org.webdevandsausages.events.service.GetContactEmailsService

object PostSpam : ApiRouteWithGraphqlConfig {
    private var emailService: EmailService? = null
    private var contacts: GetContactEmailsService? = null

    operator fun invoke(emailService: EmailService, contacts: GetContactEmailsService): PostSpam {
        this.emailService = emailService
        this.contacts = contacts
        return this
    }

    private fun handleSpamming(): HttpHandler = { req: Request ->
        this.contacts!!.invoke().fold({
            it.forEach {
                runBlocking {
                    delay(100)
                    println(it)
                }
            }
            Response(Status.OK)
        }, {
            Response(Status.INTERNAL_SERVER_ERROR)
        })
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
