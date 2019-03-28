package org.webdevandsausages.events.dao

import arrow.core.*
import meta.enums.EventStatus
import meta.tables.Event
import meta.tables.Participant
import meta.tables.records.EventRecord
import org.jooq.Condition
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.TableField
import org.jooq.impl.DSL
import org.simpleflatmapper.jdbc.JdbcMapperFactory
import org.simpleflatmapper.util.TypeReference
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.EventInDto
import org.webdevandsausages.events.dto.EventUpdateInDto
import kotlin.streams.toList

typealias EventUpdate = Pair<TableField<EventRecord, Any>, Any>
typealias EventUpdates = List<EventUpdate>

class EventCRUD(configuration: Configuration) {
    val db = DSL.using(configuration)

    val mapperInstance = JdbcMapperFactory.newInstance()

    fun findAllWithParticipants(status: String?, context: DSLContext = db): List<EventDto> {
        val resultSet = context.use { ctx ->
            ctx.select()
                .from(Event.EVENT)
                .leftJoin(Participant.PARTICIPANT)
                .on(Event.EVENT.ID.eq(Participant.PARTICIPANT.EVENT_ID))
                .apply {
                    if (status != null)
                        where(hasStatus(EventStatus.valueOf(status.toUpperCase())))
                }
                .orderBy(Event.EVENT.ID) // This is a crucial step to prevent simpleflatmapper creating duplicates // Check: https://www.petrikainulainen.net/programming/jooq/jooq-tips-implementing-a-read-only-one-to-many-relationship/
                .fetchResultSet()
        }

        val jdbcMapper = mapperInstance
            .addKeys(Event.EVENT.ID.name, Participant.PARTICIPANT.ID.name)
            .newMapper(object : TypeReference<Pair<meta.tables.pojos.Event, List<meta.tables.pojos.Participant>>>() {})

        return Try {
            jdbcMapper.stream(resultSet).map { EventDto(it.first, it.second) }.toList()
        }.getOrDefault { emptyList() }
    }

    private fun hasStatus(value: EventStatus): Condition = Event.EVENT.STATUS.eq(value)

    fun findByIdOrLatest(id: Long? = null, context: DSLContext = db): Option<EventDto> {
        return Try {
            val resultSet = context.use { ctx ->
                ctx.select()
                    .from(Event.EVENT)
                    .leftJoin(Participant.PARTICIPANT)
                    .on(Event.EVENT.ID.eq(Participant.PARTICIPANT.EVENT_ID))
                    .apply {
                        when (id) {
                            is Long -> where(Event.EVENT.ID.eq(id))
                            else -> where(hasStatus(EventStatus.OPEN))
                                .or(hasStatus(EventStatus.VISIBLE))
                                .or(hasStatus(EventStatus.OPEN_WITH_WAITLIST))
                                .or(hasStatus(EventStatus.OPEN_FULL))
                                .or(hasStatus(EventStatus.CLOSED_WITH_FEEDBACK))
                        }
                    }
                    .orderBy(Event.EVENT.ID) // This is a crucial step to prevent simpleflatmapper creating duplicates // Check: https://www.petrikainulainen.net/programming/jooq/jooq-tips-implementing-a-read-only-one-to-many-relationship/
                    .fetchResultSet()
            }
            val jdbcMapper = mapperInstance
                .addKeys(Event.EVENT.ID.name, Participant.PARTICIPANT.ID.name)
                .newMapper(object :
                    TypeReference<Pair<meta.tables.pojos.Event, List<meta.tables.pojos.Participant>>>() {})

            jdbcMapper.stream(resultSet).peek {
            }.map { EventDto(it.first, it.second) }.toList().firstOrNull()
        }.getOrDefault { null }.toOption()
    }

    // can handle an arbitrary number of updates
    fun update(id: Long?, eventIn: EventUpdateInDto, context: DSLContext = db): Option<EventDto> {
        return context.transactionResult { configuration ->
            val transaction = DSL.using(configuration)
            val eventRecord = transaction.selectFrom(Event.EVENT).where(Event.EVENT.ID.eq(id)).fetchOne()
            eventRecord.from(eventIn)
            val nonNullFields = eventRecord.fields().filter { eventRecord.get(it) != null }
            eventRecord.store(nonNullFields)
            findByIdOrLatest(id, transaction)
        }
    }

    fun findByParticipantToken(registrationToken: String, context: DSLContext = db): Option<EventDto> =
        context.use { ctx ->
            ctx.select(*Event.EVENT.fields())
                .from(Event.EVENT)
                .leftJoin(Participant.PARTICIPANT)
                .on(Event.EVENT.ID.eq(Participant.PARTICIPANT.EVENT_ID))
                .where(Participant.PARTICIPANT.VERIFICATION_TOKEN.eq(registrationToken))
                .fetchAny()
                .into(meta.tables.pojos.Event::class.java)
        }.toOption().flatMap {
            findByIdOrLatest(it.id)
        }

    fun create(event: EventInDto, context: DSLContext = db): Option<EventDto> = context.use { ctx ->
        val newRecord = ctx.newRecord(Event.EVENT)
        newRecord.from(event)
        newRecord.store()

        EventDto(
            event = newRecord.into(meta.tables.pojos.Event::class.java)
        ).toOption()
    }
}

val EventCRUD.field get() = Event.EVENT
