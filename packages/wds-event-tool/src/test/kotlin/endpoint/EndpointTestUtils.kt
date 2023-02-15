package endpoint

import io.kotlintest.shouldBe
import io.mockk.mockk
import org.http4k.core.Request
import org.http4k.core.Response
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.utils.toJsonTree

fun getRouterToTest() = Router(
    getEvents = mockk(relaxed = true),
    getCurrentEvent = mockk(relaxed = true),
    getEventById = mockk(relaxed = true),
    getRegistration = mockk(relaxed = true),
    createRegistration = mockk(relaxed = true),
    cancelRegistration = mockk(relaxed = true),
    createEvent = mockk(relaxed = true),
    updateEvent = mockk(relaxed = true),
    createContact = mockk(relaxed = true),
    emailService = mockk(relaxed = true),
    getContactEmails = mockk(relaxed = true),
    getEventParticipants = mockk(relaxed = true),
    createBlacklist = mockk(relaxed = true),
    unsubscribe = mockk(relaxed = true)
)

fun Response.expectJsonResponse(expectedBody: String? = null) {
    this.header("Content-Type").shouldBe("application/json; charset=utf-8")
    if (expectedBody != null) {
        this.bodyString().toJsonTree().shouldBe(expectedBody.toJsonTree())
    }
}

fun Request.withApiKey() = this.header("wds-key", "wds-secret")
