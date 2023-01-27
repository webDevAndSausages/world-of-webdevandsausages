package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.michaelbull.result.fold
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.service.CreateBlacklistService
import org.webdevandsausages.events.service.EmailService
import org.webdevandsausages.events.service.GetContactEmailsService

object PostBlacklist : ApiRouteWithGraphqlConfig {
    private var createBlacklist: CreateBlacklistService? = null

    operator fun invoke(createBlacklist: CreateBlacklistService): PostBlacklist {
        this.createBlacklist = createBlacklist
        return this
    }

    private fun handleAddToBlacklist(): HttpHandler = { req: Request ->
        this.createBlacklist!!.invoke("leo@leomelin.com").fold(
            { Response(Status.OK) },
            { Response(Status.INTERNAL_SERVER_ERROR) }
        )
    }

    override val route: ContractRoute = "/blacklist" meta {
        summary = "Add email address to blacklist"
        returning(Status.OK to "Blacklist addition complete")
        returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
    } bindContract Method.POST to handleAddToBlacklist()

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        // TODO
    }

}
