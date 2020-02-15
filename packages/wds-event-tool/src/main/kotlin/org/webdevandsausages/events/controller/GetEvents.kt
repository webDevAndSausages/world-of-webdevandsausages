package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import meta.enums.EventStatus
import org.http4k.contract.ContractRoute
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
import org.webdevandsausages.events.service.GetEventsService
import org.webdevandsausages.events.utils.WDSJackson.auto

object GetEvents: ApiRouteWithGraphqlConfig {
    private val EventsLens = Body.auto<List<EventOutDto>>().toLens()
    private var getEvents: GetEventsService? = null

    operator fun invoke(getEvents: GetEventsService): GetEvents {
        this.getEvents = getEvents
        return this
    }

    private fun handleGetEvents(): HttpHandler = { req: Request ->
        val status = Router.optionalStatusQuery(req).takeIf { v ->
            EventStatus.values()
                .filter { e ->
                    e.name == v?.toUpperCase()
                }.isNotEmpty()
        }
        EventsLens(
            getEvents!!(status).map { EventOutDto(it) },
            Response(Status.OK)
        )
    }

    override val route: ContractRoute =
        "/events" meta {
            summary = "Get all events and participants, with option to filter by status"
            queries += Router.optionalStatusQuery
        } bindContract GET to handleGetEvents()

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        query("allEvents") {
            resolver { status: String? ->
                getEvents!!(status).map { EventOutDto(it) }
            }
        }
    }
}
