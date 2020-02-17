package org.webdevandsausages.events.graphql

import com.apurebase.kgraphql.schema.Schema
import org.http4k.core.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.webdevandsausages.events.domain.EventError
import org.webdevandsausages.events.domain.WDSException
import org.webdevandsausages.events.utils.WDSJackson.auto
import org.webdevandsausages.events.utils.toJson

data class Query(
    val query: String,
    val variables: Any
)

fun Throwable.getStatus(): Status = when (this.message) {
    EventError.NotFound.message -> Status.NOT_FOUND
    else -> Status.INTERNAL_SERVER_ERROR
}

object GraphqlRouter {
    private val queryLens = Body.auto<Query>().toLens()
    operator fun invoke(schema: Schema): RoutingHttpHandler = routes(
        "/" bind Method.POST to { req: Request ->
            val (query, variables) = queryLens(req)
            schema.run {
                try {
                    execute(query, variables.toJson()).let {
                        Response(Status.OK).body(it)
                    }
                } catch (e: WDSException) {
                    Response(e.status).body(
                        """
                        {
                            "errors": "${e.message}",
                            "code": "${e.code}"
                        }
                    """.trimIndent()
                    )
                }
            }
        }
    )
}