package org.webdevandsausages.events.controller

import arrow.core.Either
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.error.toResponse
import org.webdevandsausages.events.service.GetEventByIdService
import org.webdevandsausages.events.service.GetEventsService
import org.webdevandsausages.events.utils.WDSJackson.auto

object GetEvent : ApiRouteWithGraphqlConfig {
    private val EventLens = Body.auto<EventOutDto>().toLens()

    private var getEventById: GetEventByIdService? = null

    operator fun invoke(getEventById: GetEventByIdService): GetEvent {
        this.getEventById = getEventById
        return this
    }

    private fun handleGetEventById(id: Long): HttpHandler =
        { _: Request ->
            getEventById!!(id).fold(
                { it.toResponse() },
                {
                    EventLens(
                        EventOutDto(it),
                        Response(Status.OK)
                    )
                }
            )
        }

    override val route: ContractRoute = "events" / Router.eventIdParam meta {
        summary = "Get event by id"
        returning(Status.OK to "Event found")
        returning(Status.NOT_FOUND to "The event does not exist.")
    } bindContract GET to ::handleGetEventById

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        query("events") {
            resolver { id: Long ->
                getEventById!!(id).fold(
                    { throw it },
                    { EventOutDto(it) }
                )
            }
        }
    }
}