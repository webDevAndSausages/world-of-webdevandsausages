package org.webdevandsausages.events.config

val local = AppConfig(
    logConfig = "log4j2.yaml",
    db = DbConfig(
        url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:45433/wds_db",
        user = "wds",
        password = "password",
        driver = ""
    ),
    port = 5000,
    secrets = null
)