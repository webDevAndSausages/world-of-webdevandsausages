package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.string
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.domain.toResponse
import org.webdevandsausages.events.service.CancelRegistrationService

object DeleteRegistration: ApiRouteWithGraphqlConfig {
    private val registrationToken = Path.string().of("token")
    private var cancelRegistration: CancelRegistrationService? = null

    operator fun invoke(cancelRegistration: CancelRegistrationService): DeleteRegistration {
        this.cancelRegistration = cancelRegistration
        return this
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleCancellation(token: String): HttpHandler = { _: Request ->
        cancelRegistration!!(token)  .fold(
            { it.toResponse() },
            {
                Router.cancelRegistrationResponseLens(
                    it,
                    Response(Status.OK)
                )
            }
        )
    }

    override val route: ContractRoute = "/events/registrations" / registrationToken meta {
        summary = "Cancel user registration"
        returning(Status.OK to "Registration to the event has been cancelled.")
        returning(Status.NOT_FOUND to "The event is closed or non-existent.")
        returning(Status.INTERNAL_SERVER_ERROR to "Some internal error occurred")
        returning(Status.UNPROCESSABLE_ENTITY to "Participant was already cancelled")
    } bindContract Method.DELETE to ::handleCancellation

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        mutation("cancelRegistration") {
            resolver { token: String ->
                cancelRegistration!!(token).fold(
                    { throw it },
                    { it }
                )
            }
        }
    }
}

