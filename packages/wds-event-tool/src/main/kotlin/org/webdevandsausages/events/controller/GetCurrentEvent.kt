package org.webdevandsausages.events.controller

import arrow.core.Either
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.error.toResponse
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.service.GetCurrentEventService
import org.webdevandsausages.events.utils.WDSJackson.auto

object GetCurrentEvent {
    private val EventLens = Body.auto<EventOutDto>().toLens()

    fun route(getCurrentEvent: GetCurrentEventService): ContractRoute {

        fun handleGetCurrentEvent() = { _: Request ->
            getCurrentEvent().let {
                when (it) {
                    is Either.Left -> it.a.toResponse()
                    is Either.Right -> EventLens(
                        EventOutDto(it.b),
                        Response(Status.OK)
                        )
                }
            }
        }

        return "events/current" meta {
            summary = "Get latest publishable event"
            returning("Latest event found." to Status.OK)
            returning("The event is closed or non-existent." to Status.NOT_FOUND)
        } bindContract GET to handleGetCurrentEvent()
    }
}