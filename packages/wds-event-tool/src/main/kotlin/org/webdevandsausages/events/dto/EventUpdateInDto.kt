package org.webdevandsausages.events.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.CaseFormat
import meta.enums.EventStatus
import meta.tables.pojos.Event
import meta.tables.records.EventRecord
import org.jooq.TableField
import org.webdevandsausages.events.dao.EventUpdate
import org.webdevandsausages.events.dao.EventUpdates
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.math.max
import kotlin.reflect.full.declaredMemberProperties

data class EventUpdateInDto(
    val name: String? = null,
    val sponsor: String? = null,
    val contact: String? = null,
    val date: Timestamp? = null,
    val details: String? = null,
    val location: String? = null,
    val status: EventStatus? = null,
    val maxParticipants: Int? = null,
    val registrationOpens: Timestamp? = null,
    val volume: Int? = null,
    val sponsorLink: String? = null
)

@Suppress("UNCHECKED_CAST")
fun EventUpdateInDto.toEventUpdates(): EventUpdates =
    this.javaClass.kotlin.declaredMemberProperties.fold(
        listOf<EventUpdate>(),
        { acc: List<EventUpdate>, i ->
            val value = i.get(this)
            if (value != null)
                acc.plus(
                    meta.tables.Event.EVENT.field(
                        CaseFormat.LOWER_CAMEL.to(
                            CaseFormat.LOWER_UNDERSCORE,
                            i.name
                        )
                    ) to value
                ) as EventUpdates
            else
                acc
        })

