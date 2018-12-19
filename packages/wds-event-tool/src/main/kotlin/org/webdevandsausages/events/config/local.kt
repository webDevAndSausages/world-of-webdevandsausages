package org.webdevandsausages.events.config

val local = AppConfig(
    logConfig = "log4j2-local.yaml",
    db = DbConfig(
        url = "jdbc:postgresql://localhost:5432/wds_db",
        user = "wds",
        password = "password",
        driver = ""
    ),
    port = 5000
)