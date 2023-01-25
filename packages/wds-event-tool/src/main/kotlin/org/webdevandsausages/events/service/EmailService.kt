package org.webdevandsausages.events.service

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import com.amazonaws.services.simpleemail.model.Content
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.runCatching
import io.pebbletemplates.pebble.PebbleEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import meta.enums.ParticipantStatus
import meta.tables.pojos.Event
import org.jsoup.Jsoup
import org.webdevandsausages.events.config.Secrets
import org.webdevandsausages.events.dao.ContactCRUD
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

class EmailService(private val secrets: Secrets?, private val contactCRUD: ContactCRUD) :
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val logger = createLogger()

    private val client by lazy {
        runCatching {
            AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1).build()
        }.getOr {
            logger.error("Could not initialize ses")
            null
        }
    }

    fun sendMail(email: String, subject: String, templateName: String, emailData: Map<String, String>) =
        launch {
            if (!contactCRUD.isEmailBlacklisted(email)) {
                logger.info("Starting email sending process")

                val engine = PebbleEngine.Builder().build()
                val compiledTemplate = engine.getTemplate("email-templates/${templateName}.html")
                val writer = StringWriter()
                compiledTemplate.evaluate(writer, emailData)

                val output = writer.toString()
                val plainTextOutput = Jsoup.parse(output).wholeText()
                logger.info("Email templates compiled")

                val request = SendEmailRequest().withDestination(Destination().withToAddresses(email)).withMessage(
                    Message().withBody(
                        Body().withHtml(Content().withCharset("UTF-8").withData(output))
                            .withText(Content().withCharset("UTF-8").withData(plainTextOutput))
                    ).withSubject(Content().withCharset("UTF-8").withData(subject))
                ).withSource("noreply@webdevandsausages.org")
                logger.info("Email request built")

                if (client != null) {
                    logger.info("Send the email...")
                    client?.sendEmail(request)
                    logger.info("Email sent to ${email}")
                } else {
                    logger.error("Email client not initialized.. Cannot send email to ${email}")
                }
            } else {
                logger.info("Email address ${email} was blacklisted. Not proceeding with sending email.")
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
            "Web Dev & Sausages Registration",
            "registration_confirmation",
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
            "Web Dev & Sausages Registration",
            "cancellation_confirmation",
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
            "Web Dev & Sausages Registration",
            "registration_update",
            emailData
        )
    }

    fun sendMailingListJoinConfirmation(contact: ContactDto) {

        logger.info("Dispatching mailing list join confirmation email to ${contact.email}")

        sendMail(
            contact.email,
            "Wed dev & sausage's mailing list confirmation",
            "mailing_list_join_confirmation",
            emptyMap()
        )
    }
}
