package org.webdevandsausages.events.graphql

import com.apurebase.kgraphql.KGraphQL
import com.apurebase.kgraphql.schema.Schema
import meta.enums.EventStatus
import meta.enums.ParticipantStatus
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.dto.EventOutDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.dto.RegistrationOutDto
import org.webdevandsausages.events.error.EventError
import org.webdevandsausages.events.error.RegistrationError
import java.sql.Timestamp

fun createSchema(configs: List<ApiRouteWithGraphqlConfig>
): Schema {
    return KGraphQL
        .schema {
            //configure method allows you customize schema behaviour
            configure {
                useDefaultPrettyPrinter = true
            }

            configs.forEach { it.config(this) }

            type<EventOutDto>()

            type<RegistrationOutDto>()

            stringScalar<Timestamp> {
                deserialize = { ts : String -> Timestamp.valueOf(ts) }
                serialize = Timestamp::toString
            }

            enum<EventStatus>{
                description = "The current state of the event."
                value(EventStatus.OPEN){
                    description = "Event is open for registration"
                }
                value(EventStatus.CANCELLED){
                    description = "Event is cancelled"
                }
                value(EventStatus.CLOSED){
                    description = "Event has passed"
                }
                value(EventStatus.CLOSED_WITH_FEEDBACK){
                    description = "Event has passed but feedback form is open"
                }
                value(EventStatus.OPEN_FULL){
                    description = "Event is full and no further registrations will be accepted"
                }
                value(EventStatus.OPEN_WITH_WAITLIST){
                    description = "Event is full and registrations will be wait-listed"
                }
                value(EventStatus.PLANNING){
                    description = "Event is in planning stages"
                }
                value(EventStatus.VISIBLE){
                    description = "Event is visible but not open for registration"
                }
            }

            enum<ParticipantStatus> {
                description = "Status of participant in event"
                value(ParticipantStatus.REGISTERED){
                    description = "Accepted into an event"
                }
                value(ParticipantStatus.WAIT_LISTED){
                    description = "In line for an event"
                }
                value(ParticipantStatus.CANCELLED){
                    description = "Cancelled participation"
                }
                value(ParticipantStatus.ORGANIZER){
                    description = "Accepted as organizer"
                }
            }
        }
}
