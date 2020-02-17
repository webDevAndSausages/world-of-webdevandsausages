package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.domain.toResponse
import org.webdevandsausages.events.service.registration.GetRegistrationService

object GetUser : ApiRouteWithGraphqlConfig {
    private var regService: GetRegistrationService? = null

    operator fun invoke(regService: GetRegistrationService): GetUser {
        this.regService = regService
        return this
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleGetUserByToken(verificationToken: String): HttpHandler =
        { _: Request ->
            regService!!.getParticipant(verificationToken).fold(
                { it.toResponse() },
                { Router.registrationResponseLens(
                    RegistrationOutDto(it),
                    Response(Status.OK)
                )}
            )
        }

    override val route: ContractRoute = "/user" / Router.verificationTokenParam meta {
        summary = "Get user by verification token"
        returning(Status.OK to "User found.")
        returning(Status.NOT_FOUND to "The user does not exist.")
        returning(Status.NOT_FOUND to "The user was not found with that token.")
        returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
    } bindContract GET to ::handleGetUserByToken

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        query("getUser") {
            resolver { verificationToken: String ->
                regService!!.getParticipant(verificationToken).fold(
                    { throw it },
                    { RegistrationOutDto(it) }
                )
            }
        }
    }
}
