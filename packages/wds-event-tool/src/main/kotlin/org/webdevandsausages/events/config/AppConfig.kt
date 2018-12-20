package org.webdevandsausages.events.config

import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultConnectionProvider
import org.jooq.impl.ThreadLocalTransactionProvider
import org.simpleflatmapper.jooq.SfmRecordMapperProvider
import java.sql.DriverManager

data class AppConfig(
    val logConfig: String,
    val db: DbConfig,
    val port: Int,
    val jooqConfiguration: Configuration = DefaultConfiguration()
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
    }
}

data class DbConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String
)