package endpoint
import arrow.core.Either
import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.webdevandsausages.events.Router
import io.mockk.every
import io.mockk.slot
import meta.enums.ParticipantStatus
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.intellij.lang.annotations.Language
import org.webdevandsausages.events.controllers.RegistrationError
import org.webdevandsausages.events.dto.ParticipantDto

class GetRegistrationEndpointTest : StringSpec() {
    lateinit var router: Router

    override fun beforeTest(description: Description) {
        router = getRouterToTest()
    }

    init {
        "should be called with id and token and return registered participant" {
            val paramSlot = slot<Long>()
            val tokenSlot = slot<String>()
            every { router.getRegistration(capture(paramSlot), capture(tokenSlot)) } returns Either.right(
                ParticipantDto(
                    name = "Joe Schmo",
                    email = "test@mail.com",
                    verificationToken = "my-token",
                    orderNumber = 10,
                    status = ParticipantStatus.REGISTERED
                  )
               )

            val request = Request(Method.GET, "/api/1.0/events/1/registrations/my-token")

            val resp = router()(request)

            @Language("JSON")
            val expectedResponseBody = """
                {
                   "registered": {
                      "email": "test@mail.com",
                      "name": "Joe Schmo",
                      "verificationToken": "my-token",
                      "status": "REGISTERED",
                      "orderNumber": 10
                  }
                }
          """.trimIndent()
            paramSlot.captured.shouldBe(1)
            tokenSlot.captured.shouldBe("my-token")
            resp.status.shouldBe(Status.OK)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 404 for missing participant" {
            every { router.getRegistration(any(), any()) } returns Either.left(RegistrationError.ParticipantNotFound)

            val request = Request(Method.GET, "/api/1.0/events/1/registrations/my-missing-token")
            val resp = router()(request)

            @Language("JSON")
            val expectedResponseBody = """
                {
                  "message":"The participant was not found with that token.",
                  "code":"NOT_FOUND"
                }
          """.trimIndent()
            resp.status.shouldBe(Status.NOT_FOUND)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 404 for missing event" {
            every { router.getRegistration(any(), any()) } returns Either.left(RegistrationError.EventNotFound)

            val request = Request(Method.GET, "/api/1.0/events/10/registrations/my-token")
            val resp = router()(request)

            @Language("JSON")
            val expectedResponseBody = """
                {
                  "message": "The event does not exist.",
                  "code": "NOT_FOUND"
                }
          """.trimIndent()
            resp.status.shouldBe(Status.NOT_FOUND)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 410 for closed event" {
            every { router.getRegistration(any(), any()) } returns Either.left(RegistrationError.EventClosed)

            val request = Request(Method.GET, "/api/1.0/events/10/registrations/my-token")
            val resp = router()(request)

            @Language("JSON")
            val expectedResponseBody = """
                {
                  "message": "The event is closed.",
                  "code": "EVENT_CLOSED_OR_MISSING"
                }
          """.trimIndent()
            resp.status.shouldBe(Status.GONE)
            resp.expectJsonResponse(expectedResponseBody)
        }

        "should return 500 for a db error" {
            every { router.getRegistration(any(), any()) } returns Either.left(RegistrationError.DatabaseError)

            val request = Request(Method.GET, "/api/1.0/events/10/registrations/my-token")
            val resp = router()(request)

            @Language("JSON")
            val expectedResponseBody = """
                {
                  "message": "A database error occurred.",
                  "code": "DATABASE_ERROR"
                }
          """.trimIndent()
            resp.status.shouldBe(Status.INTERNAL_SERVER_ERROR)
            resp.expectJsonResponse(expectedResponseBody)
        }
    }
}