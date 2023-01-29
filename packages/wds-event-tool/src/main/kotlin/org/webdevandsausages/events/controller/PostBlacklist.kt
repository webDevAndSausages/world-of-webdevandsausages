package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.michaelbull.result.fold
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson
import org.http4k.format.Jackson.auto
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.service.CreateBlacklistService
import org.webdevandsausages.events.utils.createLogger

data class Recipient(val emailAddress: String)
data class BounceComplaintInfo(
    @JsonProperty("complainedRecipients")
    @JsonAlias("bouncedRecipients")
    val recipients: List<Recipient>
)

data class BounceComplaintInDto(
    val eventType: String,
    @JsonProperty("complaint")
    @JsonAlias("bounce")
    val info: BounceComplaintInfo
)

data class SNSMessage(
    @JsonProperty("Type") val type: String,
    @JsonProperty("SubscribeURL") val subscribeUrl: String?,
    @JsonProperty("Message") val message: String
)

object PostBlacklist : ApiRouteWithGraphqlConfig {
    private var createBlacklist: CreateBlacklistService? = null
    private val SNSMessageLens = Body.auto<SNSMessage>().toLens()

    private val logger = createLogger()

    operator fun invoke(createBlacklist: CreateBlacklistService): PostBlacklist {
        this.createBlacklist = createBlacklist
        return this
    }

    private fun handleAddToBlacklist(): HttpHandler = { req: Request ->
        val SNSMessage = SNSMessageLens(req)
        if (SNSMessage.type == "SubscriptionConfirmation") {
            logger.info("Received AWS SNS Topic Subscription confirmation message")
            logger.info("${SNSMessage.type}: ${SNSMessage.subscribeUrl}")
            Response(Status.OK)
        } else if (SNSMessage.type == "Notification") {
            kotlin.runCatching {
                Jackson.mapper.readValue<BounceComplaintInDto>(SNSMessage.message)
            }.fold({
                this.createBlacklist!!.invoke(it).fold(
                    { Response(Status.OK) },
                    { Response(Status.INTERNAL_SERVER_ERROR) }
                )
            }, {
                Response(Status.BAD_REQUEST)
            })
        } else {
            Response(Status.BAD_REQUEST)
        }
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
