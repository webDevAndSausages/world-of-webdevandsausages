package endpoint

import meta.enums.EventStatus
import arrow.core.Either
import com.natpryce.hamkrest.assertion.assertThat
import io.kotlintest.Description
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import meta.tables.pojos.Event
import org.http4k.core.*
import org.http4k.hamkrest.hasBody
import org.intellij.lang.annotations.Language
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.error.EventError
import java.sql.Timestamp
import java.time.LocalDateTime
import org.webdevandsausages.events.graphql.Query
import org.webdevandsausages.events.utils.toJson

class EventGraphqlEndpoinntTest : StringSpec() {
    lateinit var router: Router

    override fun beforeTest(description: Description) {
        router = getRouterToTest()
    }

    private val TIMESTAMP = Timestamp.valueOf(LocalDateTime.now().minusDays(4))

    init {
        "currentEvent resolver should return requested properties with correct id" {
            every { router.getCurrentEvent() } returns Either.right(
                EventDto(
                    event = Event(
                        1,
                        "test_event",
                        "sponsor",
                        "esa",
                        TIMESTAMP,
                        "details",
                        "tamperee",
                        EventStatus.VISIBLE,
                        50,
                        TIMESTAMP,
                        TIMESTAMP,
                        TIMESTAMP,
                        1,
                        null
                    )
                )
            )

            val query = Query("{currentEvent{contact id name}}", "{}").toJson()

            val request = Request(Method.POST, "/graphql").body(query)

            val resp = router(null)(request)

            @Language("JSON")
            val expected = """
            {
              "data" : {
                "currentEvent" : {
                  "contact" : "esa",
                  "id" : 1,
                  "name" : "test_event"
                }
              }
            }
            """.trimIndent()

            resp.status.shouldBe(Status.OK)
            assertThat(resp, hasBody(expected))
        }

        "currentEvent resolver should return 404 error when event is not found" {
            every { router.getCurrentEvent() } returns Either.left(EventError.NotFound)

            val query = Query("{currentEvent{contact id name}}", "{}").toJson()

            val request = Request(Method.POST, "/graphql").body(query)

            val resp = router(null)(request)

            resp.status.shouldBe(Status.NOT_FOUND)
            resp.bodyString().shouldContain(EventError.NotFound.message)
        }

        "allEvents resolver should accept variable and return event list with names" {
            // val paramSlot = slot<String>()
            every { router.getEvents(any()) } returns listOf(
                EventDto(
                    event = Event(
                        1,
                        "test_event",
                        "sponsor",
                        "esa",
                        TIMESTAMP,
                        "details",
                        "tamperee",
                        EventStatus.OPEN,
                        50,
                        TIMESTAMP,
                        TIMESTAMP,
                        TIMESTAMP,
                        1,
                        null
                    )
                )
            )

            val variables = """
                {
                    "status": "open"
                }
            """.trimIndent()

            val query = Query("query allEvents(\$status: String){allEvents(status:\$status){name}}", variables).toJson()

            val request = Request(Method.POST, "/graphql").body(query)

            val resp = router(null)(request)

            @Language("JSON")
            val expectedBody = """
                {
                  "data" : {
                    "allEvents" : [ {
                      "name" : "test_event"
                    } ]
                  }
                }
            """.trimIndent()

            resp.status.shouldBe(Status.OK)
            // paramSlot.captured.shouldBe("open")
            assertThat(resp, hasBody(expectedBody))
        }
    }
}