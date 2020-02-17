package org.webdevandsausages.events.controller

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.michaelbull.result.fold
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.Router
import org.webdevandsausages.events.dto.ContactDto
import org.webdevandsausages.events.dto.ErrorCode
import org.webdevandsausages.events.dto.ErrorOutDto
import org.webdevandsausages.events.service.CreateContactService
import org.webdevandsausages.events.utils.Read
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.webdevandsausages.events.utils.parse

object PostContact : ApiRouteWithGraphqlConfig {
    private val contactLens = Body.auto<ContactDto>().toLens()

    private var createContact: CreateContactService? = null

    operator fun invoke(createContact: CreateContactService): PostContact {
        this.createContact = createContact
        return this
    }

    private fun handleJoiningMailingList(): HttpHandler = { req: Request ->

        val contact = contactLens(req)

        val validation = parse(Read.emailRead, contact.email)
        validation.fold(
            {
                Router.errorResponseLens(
                    ErrorOutDto(
                        "The email address is not valid",
                        ErrorCode.INVALID_EMAIL
                    ),
                    Response(Status.UNPROCESSABLE_ENTITY)
                )
            },
            {
                createContact!!(contact).fold(
                    { Response(it.status) },
                    { throw it })
            })
    }

    override val route: ContractRoute = "/contacts" meta {
        summary = "Create contact for the mailing list"
        receiving(contactLens)
        returning(Status.CREATED to "Person has been added to the mailing list.")
        returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
        returning(Status.UNPROCESSABLE_ENTITY to "The email address is not valid")
    } bindContract Method.POST to handleJoiningMailingList()

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        mutation("createContact") {
            resolver { email: String, phone: String?, firstName: String?, lastName: String?, subscribe: Boolean ->
                parse(Read.emailRead, email).fold(
                    {
                        throw Exception("The email address is not valid")
                    },
                    {
                        val contact = ContactDto(
                            email = email,
                            phone = phone,
                            firstName = firstName,
                            lastName = lastName,
                            subscribe = subscribe
                        )
                        createContact!!(contact).fold(
                            { it },
                            { throw it }
                        )
                    })
            }
        }
    }

}
