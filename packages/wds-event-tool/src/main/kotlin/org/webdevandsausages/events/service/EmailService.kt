package org.webdevandsausages.events.service

import arrow.core.Try
import arrow.core.getOrDefault
import com.sendgrid.Email
import com.sendgrid.Mail
import com.sendgrid.Method
import com.sendgrid.Personalization
import com.sendgrid.Request
import com.sendgrid.SendGrid
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import meta.enums.ParticipantStatus
import meta.tables.pojos.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.webdevandsausages.events.config.Secrets
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.utils.prettified

class EmailService(private val secrets: Secrets?) {
    private val logger: Logger = LoggerFactory.getLogger("email service")

    private val sg by lazy {
        Try {
            SendGrid(this.secrets?.sendgridApiKey)
        }.getOrDefault {
            logger.error("Could not initialize sendgrid")
            null
        }
    }

    fun sendMail(email: String, name: String, subject: String, templateId: String, emailData: Map<String, String>) =
        GlobalScope.launch {
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
        val sponsor = if (event.sponsor != null) event.sponsor else "Anonymous"
        val emailData = mapOf(
            "action" to status.toText,
            "datetime" to event.date.prettified,
            "location" to event.location,
            "token" to participantDto.verificationToken,
            "sponsor" to sponsor
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
}