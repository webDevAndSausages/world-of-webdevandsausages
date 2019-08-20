package endpoint

import arrow.core.Either
import com.natpryce.hamkrest.assertion.assertThat
import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import meta.enums.ParticipantStatus
import meta.tables.pojos.Participant
import org.http4k.core.*
import org.http4k.hamkrest.hasBody
import org.intellij.lang.annotations.Language
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ParticipantDto
import java.sql.Timestamp
import java.time.LocalDateTime
import org.webdevandsausages.events.graphql.Query
import org.webdevandsausages.events.utils.toJson

class RegistrationGraphqlEndpoinntTest : StringSpec() {
    lateinit var router: Router

    override fun beforeTest(description: Description) {
        router = getRouterToTest()
    }

    private val TIMESTAMP = Timestamp.valueOf(LocalDateTime.now())

    init {
        "createRegistration resolver should create and return new registration" {
            every { router.createRegistration(any()) } returns Either.right(
                ParticipantDto(
                    participant = Participant(
                        1,
                        "Esa",
                        "Kallio",
                        "esa.kallio@gmail.com",
                        "Gofore",
                        "token",
                        1000,
                        1,
                        ParticipantStatus.REGISTERED,
                        TIMESTAMP,
                        TIMESTAMP

                    )
                )
            )

            val query = Query(
                "mutation createRegistration{createRegistration(eventId:1,email:\"esa.kallio@gofore.com\",firstName:\"Esa\",lastName:\"Kallio\",affiliation:\"Gofore\",subscribe:false){registered{email}}}",
                "{}"
            ).toJson()

            val request = Request(Method.POST, "/graphql").body(query)

            val resp = router(null)(request)

            @Language("JSON")
            val expected = """
            {
              "data" : {
                "createRegistration" : {
                  "registered" : {
                    "email" : "esa.kallio@gmail.com"
                  }
                }
              }
            }
            """.trimIndent()

            resp.status.shouldBe(Status.OK)
            assertThat(resp, hasBody(expected))
        }
    }
}

