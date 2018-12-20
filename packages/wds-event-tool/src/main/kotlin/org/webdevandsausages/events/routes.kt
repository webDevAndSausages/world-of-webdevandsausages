package org.webdevandsausages.events

import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.webdevandsausages.events.services.EventService
import org.webdevandsausages.events.services.FirebaseService
import org.webdevandsausages.events.utils.toJson

fun getRoutes() = listOf(
    "/events" bind GET to { Response(OK).body(FirebaseService.getAllEvents()?.toJson() ?: "") },
    "/api/events" bind GET to { Response(OK).body(EventService.getEvents()?.toJson() ?: "") }

)
