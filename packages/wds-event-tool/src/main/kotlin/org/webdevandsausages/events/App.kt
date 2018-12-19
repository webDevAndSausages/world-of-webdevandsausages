package org.webdevandsausages.events

import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import org.apache.logging.log4j.core.config.Configurator
import org.webdevandsausages.events.config.AppConfig
import org.webdevandsausages.events.config.local

fun main(args: Array<String>) {
    val server = startApp(local)
    server.block()
}

fun startApp(config: AppConfig): Http4kServer {
    Configurator.initialize(null, config.logConfig)
    val logger = LoggerFactory.getLogger("main")
    logger.info("Starting server...")
    val app = routes(*getRoutes().toTypedArray())
    val server = app.asServer(Jetty(config.port)).start()
    logger.info("Server started on port ${config.port}")
    return server
}