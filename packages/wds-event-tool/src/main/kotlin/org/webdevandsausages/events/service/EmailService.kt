package org.webdevandsausages.events.service

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import com.amazonaws.services.simpleemail.model.Content
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.runCatching
import com.sendgrid.*
import io.pebbletemplates.pebble.PebbleEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import meta.enums.ParticipantStatus
import meta.tables.pojos.Event
import org.webdevandsausages.events.config.Secrets
import org.webdevandsausages.events.dto.ContactDto
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.service.registration.toText
import org.webdevandsausages.events.utils.createLogger
import org.webdevandsausages.events.utils.prettified
import java.io.StringWriter


val SUBJECT_INTRO = "Web Dev and Sausages:"

val WAIT_LISTED_SUBJECT = "$SUBJECT_INTRO You are currently on the waiting list."
val REGISTERED_SUBJECT = "$SUBJECT_INTRO You are successfully registered!"

class EmailService(private val secrets: Secrets?) : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val logger = createLogger()

    private val sg by lazy {
        runCatching {
            SendGrid(this.secrets?.sendgridApiKey)
        }.getOr {
            logger.error("Could not initialize sendgrid")
            null
        }
    }

    fun sendMail2(email: String, name: String, subject: String, templateId: String, emailData: Map<String, String>) =
        launch {
            val engine = PebbleEngine.Builder().build()
            val compiledTemplate = engine.getTemplate("email-templates/event_invitation.html")
            val writer = StringWriter()
            compiledTemplate.evaluate(writer, emailData)

            val output = writer.toString()

            val client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1).build()
            val request = SendEmailRequest().withDestination(Destination().withToAddresses(email)).withMessage(
                Message().withBody(
                    Body().withHtml(Content().withCharset("UTF-8").withData(output))
                        .withText(Content().withCharset("UTF-8").withData("Hello plain text"))
                ).withSubject(Content().withCharset("UTF-8").withData(subject))
            ).withSource("noreply@webdevandsausages.org")
            client.sendEmail(request)
            logger.info("Email sent to ${email}")
        }

    fun sendMail(email: String, name: String, subject: String, templateId: String, emailData: Map<String, String>) =
        launch {
            // this uses the new version 3 api for Sendgrid
            // we need to copy over our old templates in Sendgrid to work in new api
            if (sg != null) {
                val to = Email().apply {
                    setName(name)
                    setEmail(email)
                }

                val from = Email().apply {
                    setName("Richard Van Camp")
                    setEmail("richard.vancamp@gmail.com")
                }

                val personalization = Personalization().apply {
                    addTo(to)
                    emailData.forEach {
                        addDynamicTemplateData(it.key, it.value)
                    }
                }

                val mail = Mail().apply {
                    setSubject(subject)
                    setFrom(from)
                    setTemplateId(templateId)
                    addPersonalization(personalization)
                }

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "mail/send"
                    body = mail.build()
                }

                try {
                    val res = sg?.api(request)
                    logger.info("Sendgrid api response ${res?.statusCode}")
                } catch (e: Exception) {
                    logger.error("Sending of email to sendgrid api failed: ${e.message}")
                }
            }
        }

    fun sendRegistrationEmail(event: Event, status: ParticipantStatus, participantDto: ParticipantDto) {
        val subject = if (status == ParticipantStatus.WAIT_LISTED) WAIT_LISTED_SUBJECT else REGISTERED_SUBJECT
        val sponsor = if (event.sponsor != null) event.sponsor else "Anonymous"
        val emailData = mapOf(
            "action" to status.toText,
            "datetime" to event.date.prettified,
            "location" to event.location,
            "token" to participantDto.verificationToken,
            "sponsor" to sponsor,
            "subject" to subject
        )

        logger.info("Dispatching registration email to ${participantDto.email}")
        sendMail(
            participantDto.email,
            participantDto.name,
            "Web Dev & Sausages Registration",
            "d-91e5bf696190444d94f13e564fee4426",
            emailData
        )

    }

    /**
     * Send confirmation email for the participant who just cancelled
     */
    fun sendCancelConfirmationEmail(eventDto: EventDto, participantDto: ParticipantDto) {
        val sponsor = if (eventDto.event.sponsor != null) eventDto.event.sponsor else "Anonymous"
        val emailData = mapOf(
            "datetime" to eventDto.event.date.prettified,
            "location" to eventDto.event.location,
            "sponsor" to sponsor
        )

        logger.info("Dispatching cancellation email to ${participantDto.email}")
        sendMail(
            participantDto.email,
            participantDto.name,
            "Web Dev & Sausages Registration",
            "d-831c9bf56bcf4401893121910f177f0a",
            emailData
        )
    }

    /**
     * Send confirmation email for the lucky one on the waiting list who just got registered after someone cancelled
     */
    fun sendRegistrationEmailForWaitListed(eventDto: EventDto, participantDto: ParticipantDto) {
        val sponsor = if (eventDto.event.sponsor != null) eventDto.event.sponsor else "Anonymous"
        val emailData = mapOf(
            "datetime" to eventDto.event.date.prettified,
            "location" to eventDto.event.location,
            "token" to participantDto.verificationToken,
            "sponsor" to sponsor
        )

        logger.info("Dispatching confirmation email (upgraded from waiting list to registered) to ${participantDto.email}")

        sendMail(
            participantDto.email,
            participantDto.name,
            "Web Dev & Sausages Registration",
            "d-f7fd0df79d1e49d39e177d599b0411e7",
            emailData
        )
    }

    fun sendMailingListJoinConfirmation(contact: ContactDto) {

        logger.info("Dispatching mailing list join confirmation email to ${contact.email}")

        sendMail(
            contact.email,
            "",
            "Wed dev & sausage's mailing list confirmation",
            "487d4c85-5cd0-4602-80e2-5120d2483f76",
            emptyMap()
        )
    }
}
