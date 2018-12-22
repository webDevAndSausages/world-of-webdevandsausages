package org.webdevandsausages.events

import org.apache.logging.log4j.core.config.Configurator
import org.flywaydb.core.Flyway
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import org.webdevandsausages.events.config.AppConfig
import org.webdevandsausages.events.config.local
import org.webdevandsausages.events.controllers.GetCurrentEventControllerImpl
import org.webdevandsausages.events.controllers.GetEventByIdControllerImpl
import org.webdevandsausages.events.controllers.GetEventsControllerImpl
import org.webdevandsausages.events.services.EventService

fun main(args: Array<String>) {
    val server = startApp(local)
    server.block()
}

fun startApp(config: AppConfig): Http4kServer {
    Configurator.initialize(null, config.logConfig)
    val logger = LoggerFactory.getLogger("main")
    logger.info("Running DB migrations...")
    val flyway = Flyway.configure().dataSource(config.db.url, config.db.user, config.db.password).load()
    flyway.migrate()
    logger.info("Starting server...")
    val app = Router(
        GetEventsControllerImpl(EventService),
        GetCurrentEventControllerImpl(EventService, logger),
        GetEventByIdControllerImpl(EventService)
    )()
    val server = app.asServer(Jetty(config.port)).start()
    logger.info("Server started on port ${config.port}")
    return server
}