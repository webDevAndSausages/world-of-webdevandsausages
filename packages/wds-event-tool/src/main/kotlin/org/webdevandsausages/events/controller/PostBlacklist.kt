package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.michaelbull.result.fold
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.service.CreateBlacklistService

data class Recipient(val emailAddress: String)
data class BounceComplaintInfo(
    @JsonProperty("complainedRecipients")
    @JsonAlias("bouncedRecipients")
    val recipients: List<Recipient>)
data class BounceComplaintInDto(
    val notificationType: String,
    @JsonProperty("complaint")
    @JsonAlias("bounce")
    val info: BounceComplaintInfo
)

object PostBlacklist : ApiRouteWithGraphqlConfig {
    private var createBlacklist: CreateBlacklistService? = null
    private val BounceComplaintLens = Body.auto<BounceComplaintInDto>().toLens()

    operator fun invoke(createBlacklist: CreateBlacklistService): PostBlacklist {
        this.createBlacklist = createBlacklist
        return this
    }

    private fun handleAddToBlacklist(): HttpHandler = { req: Request ->
        val bounceOrComplaint = BounceComplaintLens(req)
        this.createBlacklist!!.invoke(bounceOrComplaint).fold(
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
