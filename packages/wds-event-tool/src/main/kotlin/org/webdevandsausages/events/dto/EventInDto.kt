package org.webdevandsausages.events.dto

import arrow.core.Either
import com.markodevcic.kvalidation.ValidatorBase
import com.markodevcic.kvalidation.onError
import com.markodevcic.kvalidation.rules
import meta.enums.EventStatus
import org.webdevandsausages.events.domain.EventError
import org.webdevandsausages.events.utils.mapper
import java.sql.Timestamp
import java.time.LocalDateTime

data class EventInDto(
    val name: String,
    val sponsor: String,
    val contact: String,
    val date: Timestamp,
    val details: String,
    val location: String,
    val status: EventStatus = EventStatus.PLANNING,
    val maxParticipants: Int,
    val registrationOpens: Timestamp,
    val volume: Int,
    val sponsorLink: String
) {
    companion object {
        fun from(json: String): Either<EventError.ValidationError, EventInDto> {
            try {
                class EventInDtoValidator(consumer: EventInDto) : ValidatorBase<EventInDto>(consumer)

                val dto = mapper.readValue(json, EventInDto::class.java)
                val validator = EventInDtoValidator(dto)

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
                                mustBe {
                                    (it!!.after(Timestamp.valueOf(LocalDateTime.now())) && !it.before(
                                        registrationOpens
                                    ))
                                }
                            } onError {
                                errorMessage("Date must be in the future and after the registration opens date")
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
                                mustBe { (it!!.after(Timestamp.valueOf(LocalDateTime.now())) && !it.after(date)) }
                            } onError {
                                errorMessage("Registration opens must be in the future and before the event date")
                            }
                        },
                        sponsorLink to {
                            validator.forProperty { sponsorLink } rules {
                                length(255)
                            } onError {
                                errorMessage("SponsorLink max length is 255 chars")
                            }
                        }
                    ).mapNotNull { it.value() }

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
