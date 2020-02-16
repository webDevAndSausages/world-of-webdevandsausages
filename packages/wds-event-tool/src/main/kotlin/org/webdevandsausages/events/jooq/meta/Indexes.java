/*
 * This file is generated by jOOQ.
*/
package meta;


import javax.annotation.Generated;

import meta.tables.Contact;
import meta.tables.Event;
import meta.tables.FlywaySchemaHistory;
import meta.tables.Participant;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling indexes of tables of the <code>public</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index CONTACT_EMAIL_KEY = Indexes0.CONTACT_EMAIL_KEY;
    public static final Index CONTACT_PKEY = Indexes0.CONTACT_PKEY;
    public static final Index EVENT_PKEY = Indexes0.EVENT_PKEY;
    public static final Index EVENT_VOLUME_KEY = Indexes0.EVENT_VOLUME_KEY;
    public static final Index FLYWAY_SCHEMA_HISTORY_PK = Indexes0.FLYWAY_SCHEMA_HISTORY_PK;
    public static final Index FLYWAY_SCHEMA_HISTORY_S_IDX = Indexes0.FLYWAY_SCHEMA_HISTORY_S_IDX;
    public static final Index PARTICIPANT_PKEY = Indexes0.PARTICIPANT_PKEY;
    public static final Index PARTICIPANT_VERIFICATION_TOKEN_UNIQUE = Indexes0.PARTICIPANT_VERIFICATION_TOKEN_UNIQUE;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 extends AbstractKeys {
        public static Index CONTACT_EMAIL_KEY = createIndex("contact_email_key", Contact.CONTACT, new OrderField[] { Contact.CONTACT.EMAIL }, true);
        public static Index CONTACT_PKEY = createIndex("contact_pkey", Contact.CONTACT, new OrderField[] { Contact.CONTACT.ID }, true);
        public static Index EVENT_PKEY = createIndex("event_pkey", Event.EVENT, new OrderField[] { Event.EVENT.ID }, true);
        public static Index EVENT_VOLUME_KEY = createIndex("event_volume_key", Event.EVENT, new OrderField[] { Event.EVENT.VOLUME }, true);
        public static Index FLYWAY_SCHEMA_HISTORY_PK = createIndex("flyway_schema_history_pk", FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, new OrderField[] { FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.INSTALLED_RANK }, true);
        public static Index FLYWAY_SCHEMA_HISTORY_S_IDX = createIndex("flyway_schema_history_s_idx", FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, new OrderField[] { FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.SUCCESS }, false);
        public static Index PARTICIPANT_PKEY = createIndex("participant_pkey", Participant.PARTICIPANT, new OrderField[] { Participant.PARTICIPANT.ID }, true);
        public static Index PARTICIPANT_VERIFICATION_TOKEN_UNIQUE = createIndex("participant_verification_token_unique", Participant.PARTICIPANT, new OrderField[] { Participant.PARTICIPANT.VERIFICATION_TOKEN }, true);
    }
}
