package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.domain.toResponse
import org.webdevandsausages.events.service.GetCurrentEventService
import org.webdevandsausages.events.utils.WDSJackson.auto

object GetCurrentEvent: ApiRouteWithGraphqlConfig {
    val EventLens = Body.auto<EventOutDto>().toLens()
    private var getCurrentEvent: GetCurrentEventService? = null

    operator fun invoke(getCurrentEvent: GetCurrentEventService): GetCurrentEvent {
        this.getCurrentEvent = getCurrentEvent
        return this
    }

    private fun handleGetCurrentEvent() = { _: Request ->
        getCurrentEvent!!().fold(
            {it.toResponse()},
            {EventLens(
                EventOutDto(it),
                Response(Status.OK)
            )}
        )
    }

    override val route: ContractRoute  = "/events/current" meta {
            summary = "Get latest publishable event"
            returning(Status.OK to "Latest event found.")
            returning(Status.NOT_FOUND to "The event is closed or non-existent.")
        } bindContract GET to handleGetCurrentEvent()

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        query("currentEvent") {
            resolver { ->
                getCurrentEvent!!().fold(
                    { throw it },
                    { EventOutDto(it) }
                )
            }
        }
    }
}

