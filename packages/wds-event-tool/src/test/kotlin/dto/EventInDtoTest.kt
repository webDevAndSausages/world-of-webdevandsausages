package dto

import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.intellij.lang.annotations.Language
import org.webdevandsausages.events.dto.EventInDto
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

fun getTimestamp(months: Long) = Timestamp.from(LocalDateTime.now().plusMonths(months).toInstant(ZoneOffset.UTC)).toString().replace(' ','T')

class PostEventEndpointTest : StringSpec() {
    init {
        "from method happy case json" {
            @Language("JSON")
            val invalidRequestBody = """
                {
                   "name": "test2",
                   "sponsor": "Gofore",
                   "contact": "esa",
                   "date": "${getTimestamp(3)}",
                   "details": "jotain",
                   "location": "Tampere",
                   "status": "OPEN",
                   "maxParticipants": 30,
                   "registrationOpens": "${getTimestamp(2)}",
                   "volume": 12,
                   "sponsorLink": "www.link.com"
                }
          """.trimIndent()

            val result = EventInDto.from(invalidRequestBody)
            result.shouldBeRight()
            result.fold({ it }, { it }).shouldBeInstanceOf<EventInDto>()
        }

        "from method should return validation errors if json is invalid" {
            @Language("JSON")
            val invalidRequestBody = """
                {
                   "name": "test2",
                   "sponsor": "Gofore",
                   "contact": "esa",
                   "date": "${getTimestamp(1)}",
                   "details": "jotain",
                   "location": "Tampere",
                   "status": "OPEN",
                   "maxParticipants": 0,
                   "registrationOpens": "${getTimestamp(2)}",
                   "volume": 12,
                   "sponsorLink": "www.link.com"
                }
          """.trimIndent()

            val result = EventInDto.from(invalidRequestBody)
            val errorMessages = "Max participants must be greater than 0\n" +
                    "Date must be in the future and after the registration opens date\n" +
                    "Registration opens must be in the future and before the event date"

            result.shouldBeLeft()
            val msg = result.fold({ it.message }, {println(it)})
            msg.shouldBe(errorMessages)
        }
    }
}