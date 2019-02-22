package org.webdevandsausages.events.controller

import arrow.core.Either
import meta.enums.EventStatus
import meta.tables.pojos.Event
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.meta
import org.http4k.core.*
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.EventInDto
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.error.EventError
import org.webdevandsausages.events.error.toResponse
import org.webdevandsausages.events.service.CreateEventService
import org.webdevandsausages.events.utils.WDSJackson.auto
import java.sql.Timestamp

object PostEvent {
    private val eventCreateRequestLens = Body.auto<EventInDto>().toLens()
    private val EventsLens = Body.auto<EventDto>().toLens()

    fun route(createEvent: CreateEventService): ContractRoute {

        fun handlePostEvent(): HttpHandler = { req: Request ->
            createEvent(eventCreateRequestLens(req)).let { reg ->
                when (reg) {
                    is Either.Left -> reg.a.toResponse()
                    is Either.Right -> Router.eventResponseLens(
                        reg.b,
                        Response(Status.CREATED))
                }
            }
        }

        return "events" meta {
            summary = "Create new event"
            queries += Router.optionalStatusQuery
        } bindContract Method.POST to handlePostEvent()
    }
}
