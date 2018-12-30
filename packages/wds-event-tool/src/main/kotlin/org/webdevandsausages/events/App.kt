package org.webdevandsausages.events

import org.apache.logging.log4j.core.config.Configurator
import org.flywaydb.core.Flyway
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import org.webdevandsausages.events.config.AppConfig
import org.webdevandsausages.events.config.local
import org.webdevandsausages.events.dao.EventRepository
import org.webdevandsausages.events.service.EmailService
import org.webdevandsausages.events.dao.ParticipantRepository
import org.webdevandsausages.events.service.CancelRegistrationService
import org.webdevandsausages.events.service.CreateRegistrationService
import org.webdevandsausages.events.service.EmailService
import org.webdevandsausages.events.service.FirebaseService
import org.webdevandsausages.events.service.GetCurrentEventService
import org.webdevandsausages.events.service.GetEventByIdService
import org.webdevandsausages.events.service.GetEventsService
import org.webdevandsausages.events.service.GetRegistrationService
import org.webdevandsausages.events.utils.RandomWordsUtil

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
        GetEventsService(EventRepository),
        GetCurrentEventService(EventRepository, logger),
        GetEventByIdService(EventRepository),
        GetRegistrationService(EventRepository, ParticipantRepository, logger),
        CreateRegistrationService(
            EventRepository,
            ParticipantRepository,
            RandomWordsUtil,
            EmailService(config.secrets),
            FirebaseService,
            logger
        ),
        CancelRegistrationService()
    )()
    val server = app.asServer(Jetty(config.port)).start()
    logger.info("Server started on port ${config.port}")
    return server
}