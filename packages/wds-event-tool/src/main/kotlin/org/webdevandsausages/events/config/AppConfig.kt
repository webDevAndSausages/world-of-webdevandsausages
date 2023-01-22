package org.webdevandsausages.events.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultConnectionProvider
import org.jooq.impl.ThreadLocalTransactionProvider
import org.simpleflatmapper.jooq.SfmRecordMapperProvider
import org.webdevandsausages.events.utils.asResourceStream
import java.sql.DriverManager

data class AppConfig(
    val logConfig: String,
    val db: DbConfig,
    val port: Int,
    val jooqConfiguration: Configuration = DefaultConfiguration(),
    var secrets: Secrets? = null
) {
    init {
        val cp = DefaultConnectionProvider(
            DriverManager.getConnection(this.db.url, this.db.user, this.db.password)
        )
        this.jooqConfiguration
            .set(cp)
            .set(SfmRecordMapperProvider())
            .set(SQLDialect.POSTGRES)
            .set(ThreadLocalTransactionProvider(cp, true))
        val mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
        mapper.registerModule(KotlinModule())
        secrets = "secrets.yaml".asResourceStream()?.use { mapper.readValue(it, Secrets::class.java) }
        println("loading secrets")
        println(secrets)
    }
}

data class DbConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String
)

data class Secrets(val WDSApiKey: String?)
