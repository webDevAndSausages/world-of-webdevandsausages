package org.webdevandsausages.events

import org.apache.logging.log4j.core.config.Configurator
import org.flywaydb.core.Flyway
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import org.webdevandsausages.events.config.AppConfig
import org.webdevandsausages.events.config.local
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.ParticipantCRUD
import org.webdevandsausages.events.service.*
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
    // Initialize CRUD instances
    val eventCRUD = EventCRUD(local.jooqConfiguration)
    val participantCRUD = ParticipantCRUD(local.jooqConfiguration)
    val emailService = EmailService(config.secrets)
    val firebaseService = FirebaseService()
    val app = Router(
        GetEventsService(eventCRUD),
        GetCurrentEventService(eventCRUD, logger),
        GetEventByIdService(eventCRUD),
        GetRegistrationService(eventCRUD, participantCRUD, logger),
        CreateRegistrationService(
            eventCRUD,
            participantCRUD,
            RandomWordsUtil,
            emailService,
            firebaseService,
            logger
        ),
        CancelRegistrationService(eventCRUD, participantCRUD, emailService),
        CreateEventService(eventCRUD),
        UpdateEventService(eventCRUD)
    )(config.secrets)
    val server = app.asServer(Jetty(config.port)).start()
    logger.info("Server started on port ${config.port}")
    return server
}
