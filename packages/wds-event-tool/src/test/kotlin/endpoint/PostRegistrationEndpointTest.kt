package endpoint

import arrow.core.Either
import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import meta.enums.ParticipantStatus
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.intellij.lang.annotations.Language
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.error.EventError
import org.webdevandsausages.events.utils.prettified
import java.sql.Timestamp
import java.time.LocalDateTime
import kotlin.math.exp

class PostRegistrationEndpointTest : StringSpec() {
    lateinit var router: Router

    override fun beforeTest(description: Description) {
        router = getRouterToTest()
    }

    private val TIMESTAMP = Timestamp.valueOf(LocalDateTime.now().minusDays(4))

    @Language("JSON")
    private val okRequestBody =
        """
            {
                "firstName": "Joe",
                "lastName": "Schmo",
                "email": "test@mail.com",
                "affiliation": "Acme Oy",
                "subscribe": true
            }
        """.trimIndent()

    init {
        "should return registered participant on successful registration" {
            every { router.createRegistration(any()) } returns Either.right(
                ParticipantDto(
                    name = "Joe Schmo",
                    email = "test@mail.com",
                    verificationToken = "my-token",
                    orderNumber = 10,
                    status = ParticipantStatus.REGISTERED,
                    insertedOn = TIMESTAMP.prettified
                    ))

            val request = Request(Method.POST, "/api/1.0/events/1/registrations").body(okRequestBody)

            val resp = router()(request)

            @Language("JSON")
            val expectedResponseBody = """
                {
                   "registered": {
                      "email": "test@mail.com",
                      "name": "Joe Schmo",
                      "verificationToken": "my-token",
                      "affiliation": null,
                      "status": "REGISTERED",
                      "orderNumber": 10,
                      "insertedOn": "${TIMESTAMP.prettified}"
                  }
                }
          """.trimIndent()
            resp.status.shouldBe(Status.CREATED)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 422 error for invalid email" {
            @Language("JSON")
            val requestBody = """
                {
                    "firstName": "Joe",
                    "lastName": "Schmo",
                    "email": "notvalidemail",
                    "affiliation": "Acme Oy"
                }
            """.trimIndent()
            val expectedResponseBody = """
                {
                  "message": "The email address is not valid",
                  "code": "INVALID_EMAIL"
                }
          """.trimIndent()

            val request = Request(Method.POST, "/api/1.0/events/1/registrations").body(requestBody)
            val resp = router()(request)

            resp.status.shouldBe(Status.UNPROCESSABLE_ENTITY)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 404 for missing or closed event" {
            every { router.createRegistration(any()) } returns Either.left(EventError.NotFound)
            val expectedResponseBody = """
                {
                  "message": "The event is closed or non-existent.",
                  "code": "EVENT_CLOSED_OR_MISSING"
                }
          """.trimIndent()

            val request = Request(Method.POST, "/api/1.0/events/2/registrations").body(okRequestBody)

            val resp = router()(request)

            resp.status.shouldBe(Status.NOT_FOUND)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 400 when email already registered" {
            every { router.createRegistration(any()) } returns Either.left(EventError.AlreadyRegistered)
            val expectedResponseBody = """
                {
                  "message":"The email is already registered.",
                  "code":"ALREADY_REGISTERED"
                }
          """.trimIndent()

            val request = Request(Method.POST, "/api/1.0/events/2/registrations").body(okRequestBody)

            val resp = router()(request)

            resp.status.shouldBe(Status.BAD_REQUEST)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 500 when database error occurs" {
            every { router.createRegistration(any()) } returns Either.left(EventError.DatabaseError)
            val expectedResponseBody = """
                {
                  "message": "A database error occurred.",
                  "code": "DATABASE_ERROR"
                }
          """.trimIndent()

            val request = Request(Method.POST, "/api/1.0/events/2/registrations").body(okRequestBody)
            val resp = router()(request)

            resp.status.shouldBe(Status.INTERNAL_SERVER_ERROR)
            resp.expectJsonResponse(expectedResponseBody)
        }
    }
}