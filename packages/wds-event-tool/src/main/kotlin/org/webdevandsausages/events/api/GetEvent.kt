package org.webdevandsausages.events.api

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
import org.webdevandsausages.events.controllers.GetEventByIdController
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.utils.WDSJackson.auto

object GetEvent {
    private val EventLens = Body.auto<EventOutDto>().toLens()

    fun route(getEventById: GetEventByIdController, handleErrorResponse: handleErrorResponse): ContractRoute {

        fun handleGetEventById(id: Long): HttpHandler =
            { _: Request ->
                getEventById(id).let {
                    when (it) {
                        is Either.Left -> handleErrorResponse(
                            "The event does not exist.",
                            ErrorCode.NOT_FOUND,
                            Status.NOT_FOUND
                            )
                        is Either.Right -> EventLens(
                            EventOutDto(it.b),
                            Response(Status.OK)
                            )
                    }
                }
            }

        return "events" / Router.eventIdParam meta {
                summary = "Get event by id"
                returning("Event found" to Status.OK)
                returning("The event does not exist." to Status.NOT_FOUND)
            } bindContract GET to ::handleGetEventById
    }
}