package org.webdevandsausages.events.controller

import arrow.core.Either
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.error.toResponse
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.service.GetEventByIdService
import org.webdevandsausages.events.utils.WDSJackson.auto

object AdminGetEventInfo {
    private val EventLens = Body.auto<EventDto>().toLens()

    fun route(getEventById: GetEventByIdService): ContractRoute {

        fun handleGetEventById(id: Long): HttpHandler =
            { _: Request ->
                getEventById(id).let {
                    when (it) {
                        is Either.Left -> it.a.toResponse()
                        is Either.Right -> EventLens(
                            it.b,
                            Response(Status.OK)
                            )
                    }
                }
            }

        return "events" / Router.eventIdParam meta {
                summary = "Get event by id (with participant info)"
                returning( Status.OK to "Event found")
                returning(Status.NOT_FOUND to "The event does not exist.")
            } bindContract GET to ::handleGetEventById
    }
}
