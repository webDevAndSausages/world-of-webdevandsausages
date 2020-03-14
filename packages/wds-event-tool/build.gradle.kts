import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.tools.jdbc.JDBCUtils.driver
import org.gradle.jvm.tasks.Jar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.rohanprabhu.gradle.plugins.kdjooq.*

val kotlinVersion = "1.3.61"
val coroutinesVersion = "1.3.3"
val http4kVersion = "3.235.0"
val log4jVersion = "2.13.0"
val jacksonVersion = "2.10.1"
val firebaseVersion = "6.6.0"
val flywayCoreVersion = "5.2.4"
val postgresqlDriverVersion = "42.2.5"
val jooqVersion = "3.12.1"
val arrowVersion = "0.10.4"
val mockkVersion = "1.9.2"
val KGraphQLVersion = "0.6.4"

plugins {
    kotlin("jvm") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "4.0.4"
    id("org.flywaydb.flyway") version "6.2.1"
    id("org.gradle.idea")
    id("com.rohanprabhu.kotlin-dsl-jooq") version "0.4.5"
    // id("org.jmailen.kotlinter") version "1.20.1"
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

task("execute", JavaExec::class) {
    main = "org.webdevandsausages.events.AppKt"
    classpath = java.sourceSets["main"].runtimeClasspath
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    driver("org.postgresql:postgresql:42.2.5")
}

buildscript {
    val kotlinVersion = "1.3.11"
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
    /* graphql */
    implementation("com.apurebase:kgraphql:$KGraphQLVersion")
    /* Result */
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.6")
    /* Arrow new */
    implementation("io.arrow-kt:arrow-fx:$arrowVersion")
    implementation("io.arrow-kt:arrow-mtl:$arrowVersion")
    implementation("io.arrow-kt:arrow-syntax:$arrowVersion")

    /* Validation */
    compile("com.markodevcic.kvalidation:KValidation:1.0.0")
    /* tests */
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")
    testImplementation("io.kotlintest:kotlintest-assertions-arrow:3.1.11")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-dsl:$mockkVersion")
    testImplementation("org.http4k:http4k-testing-hamkrest:$http4kVersion")
}

val jooqConfig = jooqCodegenConfiguration {
    jdbc {
        driver = "org.postgresql.Driver"
        username = "wds"
        password = "password"
        url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:45433/wds_db"
        schema = "public"
    }
    generator {
        database {
            inputSchema = "public"
            forcedTypes {
                forcedType {
                    name = "varchar"
                    expression = ".*"
                    types = "JSONB?"
                }
                forcedType {
                    name = "varchar"
                    expression = ".*"
                    types = "INET"
                }
            }
            generate {
                isRelations = true
                isDeprecated = true
                isRecords = true
                isDaos = false
                isInterfaces = false
                isImmutablePojos = true
                isFluentSetters = true
            }
            target {
                packageName = "meta"
                directory = "src/main/kotlin/org/webdevandsausages/events/jooq"
            }
        }
    }
}

jooqGenerator {
    jooqEdition = JooqEdition.OpenSource
    jooqVersion = "3.12.1"
    configuration("meta", project.java.sourceSets.getByName("main")) {
        configuration = jooqConfig
    }
}

tasks.getByName<ShadowJar>("shadowJar") {
    baseName = "events"
    mergeServiceFiles()
    manifest {
        attributes(mapOf("Main-Class" to "org.webdevandsausages.events.AppKt"))
    }
}

tasks {
    "build" {
        dependsOn("shadowJar")
    }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "11"
