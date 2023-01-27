package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.michaelbull.result.fold
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ContactDto
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto
import org.webdevandsausages.events.service.CreateContactService
import org.webdevandsausages.events.service.EmailService
import org.webdevandsausages.events.utils.Read
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.webdevandsausages.events.utils.parse

object PostSpam : ApiRouteWithGraphqlConfig {
    private var emailService: EmailService? = null

    operator fun invoke(emailService: EmailService): PostSpam {
        this.emailService = emailService
        return this
    }

    private fun handleSpamming(): HttpHandler = { req: Request ->
        // TODO
        Response(Status.OK)
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
