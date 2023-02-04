package org.webdevandsausages.events.controller

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.right
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.github.michaelbull.result.fold
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.lens.Query
import org.http4k.lens.string
import org.webdevandsausages.events.ApiRouteWithGraphqlConfig
import org.webdevandsausages.events.service.EmailService
import org.webdevandsausages.events.service.UnsubscribeEmailService
import org.webdevandsausages.events.utils.decrypt
import sun.security.x509.CertificateAlgorithmId.ALGORITHM
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object GetUnsubscribe : ApiRouteWithGraphqlConfig {
    private var emailService: EmailService? = null
    private var unsubscribe: UnsubscribeEmailService? = null
    private val EmailLens = Query.string().required("hash")

    operator fun invoke(unsubscribe: UnsubscribeEmailService): GetUnsubscribe {
        this.unsubscribe = unsubscribe
        return this
    }

    private fun handleUnsubscribe(): HttpHandler = { req: Request ->
        EmailLens(req).let { hashedEmail ->
            decrypt(hashedEmail, System.getenv("PUBLIC_WDS_API_KEY")).fold({
                Response(Status.BAD_REQUEST)
            }, {
                this.unsubscribe!!.invoke(it).fold({
                    Response(Status.OK).body("Successfully unsubscribed")
                }, {
                    Response(Status.INTERNAL_SERVER_ERROR)
                })
            })
        }
    }

    override val route: ContractRoute = "/unsubscribe" meta {
        summary = "Unsubscribe email from mailing list"
        returning(Status.OK to "Successfully unsubscribed")
        returning(Status.BAD_REQUEST to "Invalid email hash.")
        returning(Status.INTERNAL_SERVER_ERROR to "A database error occurred.")
    } bindContract Method.GET to handleUnsubscribe()

    override val config: SchemaBuilder<Unit>.() -> Unit = {
        // TODO
    }

}
