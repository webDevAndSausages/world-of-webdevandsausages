package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.michaelbull.result.fold
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.json
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.service.CreateBlacklistService
import org.webdevandsausages.events.utils.createLogger
import org.webdevandsausages.events.utils.toJson

data class Recipient(val emailAddress: String)
data class BounceComplaintInfo(
    @JsonProperty("complainedRecipients")
    @JsonAlias("bouncedRecipients")
    val recipients: List<Recipient>
)

data class BounceComplaintInDto(
    val notificationType: String,
    @JsonProperty("complaint")
    @JsonAlias("bounce")
    val info: BounceComplaintInfo
)

data class SNSEndpointConfirmationInDto(
    @JsonProperty("Type") val type: String,
    @JsonProperty("SubscribeURL") val subscribeUrl: String?
)

data class SNSMessage(
    @JsonProperty("Type") val type: String,
    @JsonProperty("Message") val message: BounceComplaintInDto
)

object PostBlacklist : ApiRouteWithGraphqlConfig {
    private var createBlacklist: CreateBlacklistService? = null
    private val BounceComplaintLens = Body.auto<SNSMessage>().toLens()
    private val SNSEndpointConfirmationLens = Body.auto<SNSEndpointConfirmationInDto>().toLens()
    private val logger = createLogger()

    operator fun invoke(createBlacklist: CreateBlacklistService): PostBlacklist {
        this.createBlacklist = createBlacklist
        return this
    }

    private fun handleAddToBlacklist(): HttpHandler = { req: Request ->
        val confirmation = try {
            SNSEndpointConfirmationLens(req)
        } catch (e: Exception) {
            null
        }

        val jsonMessage = Body.json().toLens()(req)
        val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        val jsonString = objectMapper.writeValueAsString(jsonMessage)
        logger.info(jsonString)

        if (confirmation != null) {
            logger.info("Received AWS SNS Topic Subscription confirmation message")
            logger.info("${confirmation.type}: ${confirmation.subscribeUrl}")
            Response(Status.OK)
        } else {
            val bounceOrComplaint = BounceComplaintLens(req)
            this.createBlacklist!!.invoke(bounceOrComplaint.message).fold(
                { Response(Status.OK) },
                { Response(Status.INTERNAL_SERVER_ERROR) }
            )
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
