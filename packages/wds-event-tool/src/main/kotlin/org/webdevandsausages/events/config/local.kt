package org.webdevandsausages.events.config

val local = AppConfig(
    logConfig = "log4j2-local.yaml",
    db = DbConfig(
        url = "",
        driver = ""
    ),
    port = 5000
)