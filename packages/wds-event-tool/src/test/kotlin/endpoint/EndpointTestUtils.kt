package endpoint

import io.kotlintest.shouldBe
import io.mockk.mockk
import org.http4k.core.Response
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.utils.toJsonTree

fun getRouterToTest() = Router(
    getEvents = mockk(relaxed = true),
    getCurrentEvent = mockk(relaxed = true),
    getEventById = mockk(relaxed = true),
    createRegistration = mockk(relaxed = true)
  )

fun Response.expectJsonResponse(expectedBody: String? = null) {
    this.header("Content-Type").shouldBe("application/json; charset=utf-8")
    if (expectedBody != null) {
        this.bodyString().toJsonTree().shouldBe(expectedBody.toJsonTree())
    }
}
