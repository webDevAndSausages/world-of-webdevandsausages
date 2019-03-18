package org.webdevandsausages.events.dto

import arrow.core.Either
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.CaseFormat
import com.markodevcic.kvalidation.ValidatorBase
import com.markodevcic.kvalidation.onError
import com.markodevcic.kvalidation.rules
import meta.enums.EventStatus
import meta.tables.Event.EVENT
import meta.tables.pojos.Event
import meta.tables.records.EventRecord
import org.http4k.core.Response
import org.jooq.TableField
import org.webdevandsausages.events.dao.EventUpdate
import org.webdevandsausages.events.dao.EventUpdates
import org.webdevandsausages.events.error.EventError
import org.webdevandsausages.events.error.toResponse
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
) {
    companion object {
        fun from(json: String): Either<EventError.ValidationError, EventUpdateInDto> {
            try {
                class EventUpdateInDtoValidator(consumer: EventUpdateInDto) : ValidatorBase<EventUpdateInDto>(consumer)
                val dto = ObjectMapper().readValue(json, EventUpdateInDto::class.java)
                val validator = EventUpdateInDtoValidator(dto)

                with(dto) {
                    mapOf(
                        maxParticipants to {
                            validator.forProperty { maxParticipants } rules {
                                gt(0)
                            } onError {
                                errorMessage("Max participants must be greater than 0")
                            }
                        },
                        name to {
                            validator.forProperty { name } rules {
                                length(255)
                            } onError {
                                errorMessage("Name max length is 255 chars")
                            }
                        },
                        contact to {
                            validator.forProperty { contact } rules {
                                length(255)
                            } onError {
                                errorMessage("Contact max length is 255 chars")
                            }
                        },
                        date to {
                            validator.forProperty { date } rules {
                                mustBe { it!!.after(Timestamp.valueOf(LocalDateTime.now())) }
                            } onError {
                                errorMessage("Date must be in the future")
                            }
                        },
                        details to {
                            validator.forProperty { details } rules {
                                length(1024)
                            } onError {
                                errorMessage("Details max length is 1024 chars")
                            }
                        },
                        location to {
                            validator.forProperty { location } rules {
                                length(255)
                            } onError {
                                errorMessage("Location max length is 255 chars")
                            }
                        },
                        registrationOpens to {
                            validator.forProperty { registrationOpens } rules {
                                mustBe { it!!.after(Timestamp.valueOf(LocalDateTime.now())) }
                            } onError {
                                errorMessage("Registration opens must be in the future")
                            }
                        },
                        sponsorLink to {
                            validator.forProperty { sponsorLink } rules {
                                length(255)
                            } onError {
                                errorMessage("SponsorLink max length is 255 chars")
                            }
                        }
                    ).filter { it.key != null }
                        .forEach { it.value() } // Filter out null values and execute validators for non-nulls

                    val validationResult = validator.validate()

                    return Either.cond(validationResult.isValid,
                        { this },
                        { EventError.ValidationError(validationResult.validationErrors.map { it.message }.joinToString("\n")) }
                    )
                }
            } catch (e: Exception) {
                return Either.left(EventError.ValidationError("Invalid JSON input"))
            }
        }
    }
}

fun EventUpdateInDto.toEventUpdates(): EventUpdates =
    this.javaClass.kotlin.declaredMemberProperties.fold(
        listOf<EventUpdate>(),
        { acc: EventUpdates, i ->
            val value = i.get(this)
            if (value != null)
                acc.plus(
                    (EVENT.field(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, i.name)) to value)
                            as EventUpdate
                )
            else
                acc
        })
