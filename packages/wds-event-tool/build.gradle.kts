
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.tools.jdbc.JDBCUtils.driver
import org.jooq.util.jaxb.Configuration
import org.jooq.util.jaxb.Database
import org.jooq.util.jaxb.ForcedType
import org.jooq.util.jaxb.Generate
import org.jooq.util.jaxb.Generator
import org.jooq.util.jaxb.Jdbc
import org.jooq.util.jaxb.Strategy
import org.jooq.util.jaxb.Target

val kotlinVersion = "1.3.61"
val coroutinesVersion = "1.3.3"
val http4kVersion = "3.235.0"
val log4jVersion = "2.13.0"
val jacksonVersion = "2.10.1"
val flywayCoreVersion = "5.2.4"
val postgresqlDriverVersion = "42.2.5"
val jooqVersion = "3.10.1"
val arrowVersion = "0.10.4"
val mockkVersion = "1.9.2"
val KGraphQLVersion = "0.6.4"

plugins {
    kotlin("jvm") version "1.3.61"
    id("org.flywaydb.flyway") version "6.2.1"
    id("org.gradle.idea")
    id("com.rohanprabhu.kotlin-dsl-jooq") version "0.3.1"
    java
    application
}

application {
    mainClassName = "org.webdevandsausages.events.AppKt"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    driver("org.postgresql:postgresql:42.2.5")
}

buildscript {
    val kotlinVersion = "1.3.61"
    val flywayCoreVersion = "5.2.4"

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("org.flywaydb:flyway-core:$flywayCoreVersion")
        classpath("org.postgresql:postgresql:42.2.5")
    }
}

dependencies {
    /* kotlin */
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    /* http4k */
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-contract:$http4kVersion")
    implementation("org.http4k:http4k-server-jetty:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    implementation("org.http4k:http4k-client-apache:$http4kVersion")
    implementation("org.http4k:http4k-template-handlebars:$http4kVersion")
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

    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:20.0")

    /* db & jooq */
    implementation("org.flywaydb:flyway-core:$flywayCoreVersion")
    implementation("org.postgresql:postgresql:$postgresqlDriverVersion")
    implementation("org.jooq", "jooq", jooqVersion)
    implementation("org.simpleflatmapper", "sfm-jooq", "6.0.13")
    jooqGeneratorRuntime("org.postgresql:postgresql:$postgresqlDriverVersion")
    /* graphql */
    implementation("com.apurebase:kgraphql:$KGraphQLVersion")
    /* Result */
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.6")
    /* Arrow new */
    implementation("io.arrow-kt:arrow-fx:$arrowVersion")
    implementation("io.arrow-kt:arrow-mtl:$arrowVersion")
    implementation("io.arrow-kt:arrow-syntax:$arrowVersion")

    // https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-ses
    implementation("com.amazonaws:aws-java-sdk-ses:1.12.385")

    // https://mvnrepository.com/artifact/io.pebbletemplates/pebble
    implementation("io.pebbletemplates:pebble:3.2.0")

    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.15.3")

    /* Validation */
    compile("com.markodevcic.kvalidation:KValidation:1.0.0")
    /* tests */
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")
    testImplementation("io.kotlintest:kotlintest-assertions-arrow:3.1.11")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-dsl:$mockkVersion")
    testImplementation("org.http4k:http4k-testing-hamkrest:$http4kVersion")
}

val jooqConfig = Configuration()
    .withJdbc(
        Jdbc()
            .withDriver("org.postgresql.Driver")
            .withUsername("wds")
            .withPassword("password")
            .withUrl(System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:45433/wds_db")
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
                              .withDaos(false)
                              .withInterfaces(false)
                              .withImmutablePojos(true)
                              .withFluentSetters(true)
            )
            .withTarget(Target()
                            .withPackageName("meta")
                            .withDirectory("src/main/kotlin/org/webdevandsausages/events/jooq")
            )
    )



jooqGenerator {
    configuration("meta", sourceSets["main"]) {
        configuration = jooqConfig
    }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"
