package org.webdevandsausages.events.controller

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.data.Kleisli.Companion.raiseError
import arrow.effects.IO
import arrow.instances.either.monad.flatten
import meta.enums.EventStatus
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.dto.EventsOutDto
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.http4k.core.Method.GET
import org.slf4j.Logger
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dao.EventRepository
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.handleErrorResponse
import org.webdevandsausages.events.service.GetEventsCommand
import org.webdevandsausages.events.service.GetEventsService
import org.webdevandsausages.events.service.runWithError


object GetEvents {
    private val EventsLens = Body.auto<EventsOutDto>().toLens()

    fun route(eventRepo: EventRepository, logger: Logger, handleErrorResponse: handleErrorResponse): ContractRoute {

        fun handleGetEvents(): HttpHandler = { req: Request ->
            val status = Router.optionalStatusQuery(req).takeIf {
                    v -> EventStatus.values()
                .filter { e ->
                    e.name == v?.toUpperCase() }.isNotEmpty()
            }

            object : GetEventsService {
                override val getEvents = eventRepo::findAllWithParticipants
            }.run { GetEventsCommand(status).execute() }
                .runWithError(logger)
                .let { result ->
                    when(result) {
                        is Either.Right ->
                        EventsLens(
                            EventsOutDto(result.b),
                            Response(Status.OK)
                          )
                        else -> handleErrorResponse(
                            "A database error occurred.",
                            ErrorCode.DATABASE_ERROR,
                            Status.INTERNAL_SERVER_ERROR)
                    }
            }

        }

        return "events" meta {
            summary = "Get all events and participants, with option to filter by status"
            queries += Router.optionalStatusQuery
        } bindContract GET to handleGetEvents()
    }
}