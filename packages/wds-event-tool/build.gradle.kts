import com.rohanprabhu.gradle.plugins.kdjooq.generator
import com.rohanprabhu.gradle.plugins.kdjooq.jdbc
import com.rohanprabhu.gradle.plugins.kdjooq.jooqCodegenConfiguration
import org.gradle.api.internal.initialization.ClassLoaderIds.buildScript
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.util.jaxb.Database
import org.jooq.util.jaxb.ForcedType
import org.jooq.util.jaxb.Target
import org.jooq.util.jaxb.Configuration
import org.jooq.util.jaxb.Generate
import org.jooq.util.jaxb.Generator
import org.jooq.util.jaxb.Jdbc
import org.jooq.util.jaxb.Strategy
import org.jooq.util.postgres.PostgresDatabase

val kotlinVersion = "1.3.11"
val http4kVersion = "3.103.2"
val log4jVersion = "2.10.0"
val jacksonVersion = "2.9.6"
val firebaseVersion = "6.6.0"
val flywayCoreVersion = "5.2.4"
val postgresqlDriverVersion = "42.2.5"
val jooqVersion = "3.10.1"

plugins {
    kotlin("jvm") version "1.3.11"
    id("com.rohanprabhu.kotlin-dsl-jooq") version "0.3.1"
}

repositories {
    mavenCentral()
    jcenter()
}

buildscript {
    val kotlinVersion = "1.3.11"

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    }
}

dependencies {
    /* kotlin */
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    /* http4k */
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-contract:$http4kVersion")
    implementation("org.http4k:http4k-server-jetty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    implementation("org.http4k:http4k-client-apache:$http4kVersion")
    /* log4 */
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    /* jackson */
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    /* firebase */
    implementation("com.google.firebase:firebase-admin:$firebaseVersion")
    implementation("com.google.firebase:firebase-admin:$firebaseVersion")
    /* sendgrid */
    implementation("com.sendgrid:sendgrid-java:4.3.0")
    /* db & jooq */
    implementation("org.flywaydb:flyway-core:$flywayCoreVersion")
    implementation("org.postgresql:postgresql:$postgresqlDriverVersion")
    implementation("org.jooq", "jooq", jooqVersion)
    implementation("org.simpleflatmapper", "sfm-jooq", "6.0.13")
    jooqGeneratorRuntime("org.postgresql:postgresql:$postgresqlDriverVersion")
    /* tests */
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")
    testImplementation("io.mockk:mockk:1.8.13.kotlin13")
}
/*
tasks.test {
    useJUnitPlatform()
}
*/

val jooqConfig = Configuration()
    .withJdbc(
        Jdbc()
            .withDriver("org.postgresql.Driver")
            .withUsername("wds")
            .withPassword("password")
            .withUrl("jdbc:postgresql://localhost:45433/wds_db")
            .withSchema("public")
    )
    .withGenerator(
        Generator()
            .withName("org.jooq.util.DefaultGenerator")
            .withStrategy(Strategy().withName("org.jooq.util.DefaultGeneratorStrategy"))
            .withDatabase(
                Database()
                    .withName("org.jooq.util.postgres.PostgresDatabase")
                    .withInputSchema("public")
                    .withForcedTypes(
                        ForcedType()
                            .withName("varchar")
                            .withExpression(".*")
                            .withTypes("JSONB?"),
                        ForcedType()
                            .withName("varchar")
                            .withExpression(".*")
                            .withTypes("INET")
                    )
            )
            .withGenerate(
                Generate()
                              .withRelations(true)
                              .withDeprecated(true)
                              .withRecords(true)
                              .withDaos(true)
                              .withInterfaces(true)
                              .withImmutablePojos(true)
                              .withFluentSetters(true)
            )
            .withTarget(Target()
                            .withPackageName("meta")
                            .withDirectory("src/main/kotlin/org/webdevandsausages/events/jooq")
            )
    )

jooqGenerator {
    configuration("meta", project.java.sourceSets.getByName("main")) {
        configuration = jooqConfig
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"
