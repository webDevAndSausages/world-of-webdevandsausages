package endpoint

import arrow.core.Either
import com.sun.xml.internal.ws.api.message.Headers
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
import org.webdevandsausages.events.error.RegistrationCancellationError
import org.webdevandsausages.events.utils.prettified
import java.sql.Timestamp
import java.time.LocalDateTime

class DeleteRegistrationEndpointTest : StringSpec() {
    lateinit var router: Router

    override fun beforeTest(description: Description) {
        router = getRouterToTest()
    }

    private val TIMESTAMP = Timestamp.valueOf(LocalDateTime.now().minusDays(4))

    init {
        "should return cancelled participant on successful cancellation" {
            every { router.cancelRegistration(any()) } returns Either.right(
                ParticipantDto(
                    name = "Joe Schmo",
                    email = "test@mail.com",
                    verificationToken = "my-token",
                    orderNumber = 10,
                    status = ParticipantStatus.CANCELLED,
                    insertedOn = TIMESTAMP.prettified
                )
            )

            val request = Request(Method.DELETE, "/api/1.0/events/registrations/my-token").withApiKey()

            val resp = router(null)(request)

            @Language("JSON")
            val expectedResponseBody = """
                {
                  "email": "test@mail.com",
                  "name": "Joe Schmo",
                  "verificationToken": "my-token",
                  "affiliation": null,
                  "status": "CANCELLED",
                  "orderNumber": 10,
                  "insertedOn": "${TIMESTAMP.prettified}"
                }
            """.trimIndent()
            resp.status.shouldBe(Status.OK)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 404 for missing or closed event" {
            every { router.cancelRegistration(any()) } returns Either.left(RegistrationCancellationError.EventNotFound)

            val expectedResponseBody = """
                {
                  "message": "The event is closed or non-existent.",
                  "code": "EVENT_CLOSED_OR_MISSING"
                }
            """.trimIndent()

            val request = Request(Method.DELETE, "/api/1.0/events/registrations/my-token").withApiKey()

            val resp = router(null)(request)

            resp.status.shouldBe(Status.NOT_FOUND)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 404 for missing participant" {
            every { router.cancelRegistration(any()) } returns Either.left(RegistrationCancellationError.ParticipantNotFound)

            val expectedResponseBody = """
                {
                  "message": "A registration was not found with provided token.",
                  "code": "PARTICIPANT_NOT_FOUND"
                }
            """.trimIndent()

            val request = Request(Method.DELETE, "/api/1.0/events/registrations/my-token").withApiKey()

            val resp = router(null)(request)

            resp.status.shouldBe(Status.NOT_FOUND)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 500 when database error occurs" {
            every { router.cancelRegistration(any()) } returns Either.left(RegistrationCancellationError.DatabaseError)

            val expectedResponseBody = """
                {
                  "message": "A database error occurred.",
                  "code": "DATABASE_ERROR"
                }
            """.trimIndent()

            val request = Request(Method.DELETE, "/api/1.0/events/registrations/my-token").withApiKey()

            val resp = router(null)(request)

            resp.status.shouldBe(Status.INTERNAL_SERVER_ERROR)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 422 when participant was already cancelled" {
            every { router.cancelRegistration(any()) } returns Either.left(RegistrationCancellationError.ParticipantAlreadyCancelled)

            val expectedResponseBody = """
                {
                  "message": "This registration was already cancelled.",
                  "code": "ALREADY_CANCELLED"
                }
            """.trimIndent()

            val request = Request(Method.DELETE, "/api/1.0/events/registrations/my-token").withApiKey()

            val resp = router(null)(request)

            resp.status.shouldBe(Status.UNPROCESSABLE_ENTITY)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 500 on should never happen error" {
            every { router.cancelRegistration(any()) } returns Either.left(RegistrationCancellationError.ShouldNeverHappen)

            val expectedResponseBody = """
                {
                  "message": "Something weird happened. Should never happen, lol",
                  "code": "SHOULD_NEVER_HAPPEN"
                }
            """.trimIndent()

            val request = Request(Method.DELETE, "/api/1.0/events/registrations/my-token").withApiKey()

            val resp = router(null)(request)

            resp.status.shouldBe(Status.INTERNAL_SERVER_ERROR)
            resp.expectJsonResponse(expectedResponseBody)
        }
    }
}