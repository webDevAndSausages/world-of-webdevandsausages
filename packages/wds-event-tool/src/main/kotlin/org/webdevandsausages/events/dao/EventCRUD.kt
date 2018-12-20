package org.webdevandsausages.events.dao

import meta.tables.Event
import meta.tables.Participant
import meta.tables.daos.EventDao
import org.jooq.impl.DSL
import org.simpleflatmapper.jdbc.JdbcMapperFactory
import org.simpleflatmapper.util.TypeReference
import org.webdevandsausages.events.config.local
import org.webdevandsausages.events.dto.EventDto
import java.util.stream.Collectors

object EventCRUD: EventDao(local.jooqConfiguration) {
    /**
     * Custom method with custom query + mapper
     */
    fun findAllWithParticipants(): List<EventDto>? {
        val resultSet = DSL.using(configuration())
            .select().from(Event.EVENT).join(Participant.PARTICIPANT)
            .on(Event.EVENT.ID.eq(Participant.PARTICIPANT.EVENT_ID))
            .orderBy(Event.EVENT.ID) // This is a crucial step to prevent simpleflatmapper creating duplicates // Check: https://www.petrikainulainen.net/programming/jooq/jooq-tips-implementing-a-read-only-one-to-many-relationship/
            .fetchResultSet()

        val jdbcMapper = JdbcMapperFactory
            .newInstance()
            .addKeys(Participant.PARTICIPANT.ID.name)
            .newMapper(object : TypeReference<Pair<meta.tables.pojos.Event, List<meta.tables.pojos.Participant>>>() {})

        return jdbcMapper.stream(resultSet).map { EventDto(it.first, it.second) }.collect(Collectors.toList())
    }
}