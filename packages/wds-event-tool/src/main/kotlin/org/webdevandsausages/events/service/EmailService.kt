package org.webdevandsausages.events.service

import arrow.core.Try
import arrow.core.getOrDefault
import com.sendgrid.Request
import com.sendgrid.Method
import com.sendgrid.Email
import com.sendgrid.Personalization
import com.sendgrid.Mail
import org.slf4j.LoggerFactory
import org.webdevandsausages.events.config.Secrets
import com.sendgrid.SendGrid
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger

class EmailService(private val secrets: Secrets?) {
    private val logger: Logger = LoggerFactory.getLogger("email service")

    private val sg by lazy { Try {
        SendGrid(this.secrets?.sendgridApiKey)
    }.getOrDefault {
        logger.error("Could not initialize sendgrid")
        null
    }}

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
    }