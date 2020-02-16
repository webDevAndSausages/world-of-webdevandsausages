package endpoint

import com.github.michaelbull.result.Ok
import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.intellij.lang.annotations.Language
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.domain.DomainSuccess

class PostContactEndpointTest : StringSpec() {
    lateinit var router: Router

    override fun beforeTest(description: Description) {
        router = getRouterToTest()
    }

    @Language("JSON")
    private val okRequestBody =
        """
            {
                "firstName": "Joe",
                "lastName": "Schmo",
                "email": "test@mail.com"
            }
        """.trimIndent()

    init {
        "should return ok on successful registration" {
            every { router.createContact(any()) } returns Ok(DomainSuccess.Created)

            val request = Request(Method.POST, "/api/1.0/contacts").body(okRequestBody).withApiKey()

            val resp = router(null)(request)

            resp.status.shouldBe(Status.CREATED)
        }

        "should return 422 error for invalid email" {
            @Language("JSON")
            val requestBody = """
                {
                    "firstName": "Joe",
                    "lastName": "Schmo",
                    "email": "notvalidemail"
                }
            """.trimIndent()
            val expectedResponseBody = """
                {
                  "message": "The email address is not valid",
                  "code": "INVALID_EMAIL"
                }
          """.trimIndent()

            val request = Request(Method.POST, "/api/1.0/events/1/registrations").body(requestBody).withApiKey()
            val resp = router(null)(request)

            resp.status.shouldBe(Status.UNPROCESSABLE_ENTITY)
            resp.expectJsonResponse(expectedResponseBody)
        }
    }
}