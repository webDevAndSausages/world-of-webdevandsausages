package org.webdevandsausages.events.api

import arrow.core.Either
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.controllers.GetCurrentEventController
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.utils.WDSJackson.auto

object GetCurrentEvent {
    private val EventLens = Body.auto<EventOutDto>().toLens()

    fun route(getCurrentEvent: GetCurrentEventController, handleErrorResponse: handleErrorResponse): ContractRoute {

        fun handleGetCurrentEvent() = { _: Request ->
            getCurrentEvent().let {
                when (it) {
                    is Either.Left -> handleErrorResponse(
                        "The event is closed or non-existent.",
                        ErrorCode.EVENT_CLOSED_OR_MISSING,
                        Status.NOT_FOUND
                                                         )
                    is Either.Right -> EventLens(
                        EventOutDto(it.b),
                        Response(Status.OK)
                                                         )
                }
            }
        }

        return "/api/1.0/events/current" meta {
            summary = "Get latest publishable event"
            returning("Latest event found." to Status.OK)
            returning("The event is closed or non-existent." to Status.NOT_FOUND)
        } bindContract GET to handleGetCurrentEvent()
    }
}