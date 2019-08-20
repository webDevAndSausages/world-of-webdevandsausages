package org.webdevandsausages.events

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import org.http4k.contract.ContractRoute


interface ApiRouteWithGraphqlConfig {
    val route: ContractRoute
    val config: SchemaBuilder<Unit>.() -> Unit
}