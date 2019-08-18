package org.webdevandsausages.events.graphql

import com.apurebase.kgraphql.schema.Schema
import org.http4k.core.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.webdevandsausages.events.error.EventError
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
            val res = schema.runCatching {
                execute(query, variables.toJson()).let {
                    Response(Status.OK).body(it)
                }
            }
            res.fold(onSuccess = { it }, onFailure = {
                Response(it.getStatus()).body(
                    """
                        {
                            "errors": "${it.message}"
                        }
                    """.trimIndent()
                )
            })
        }
    )
}