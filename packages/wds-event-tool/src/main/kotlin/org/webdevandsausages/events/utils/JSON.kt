package org.webdevandsausages.events.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.datatype.joda.JodaModule
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings

val mapper = KotlinModule()
    .asConfigurable()
    .withStandardMappings()
    .done()
    .registerModule(JodaModule())
    .disableDefaultTyping()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true)
    .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    .configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
    .configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)

fun String.toJsonTree() = mapper.readTree(this)

fun Any.toJson(pretty: Boolean = false): String =
    if (pretty) jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this)
    else jacksonObjectMapper().writeValueAsString(this)

object WDSJackson : ConfigurableJackson(mapper)
