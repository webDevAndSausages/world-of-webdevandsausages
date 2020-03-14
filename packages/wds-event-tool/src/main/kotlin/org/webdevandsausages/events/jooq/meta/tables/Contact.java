/*
 * This file is generated by jOOQ.
 */
package meta.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import meta.Indexes;
import meta.Keys;
import meta.Public;
import meta.tables.records.ContactRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Contact extends TableImpl<ContactRecord> {

    private static final long serialVersionUID = 231154180;

    /**
     * The reference instance of <code>public.contact</code>
     */
    public static final Contact CONTACT = new Contact();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContactRecord> getRecordType() {
        return ContactRecord.class;
    }

    /**
     * The column <code>public.contact.id</code>.
     */
    public final TableField<ContactRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('contact_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>public.contact.email</code>.
     */
    public final TableField<ContactRecord, String> EMAIL = createField(DSL.name("email"), org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>public.contact.first_name</code>.
     */
    public final TableField<ContactRecord, String> FIRST_NAME = createField(DSL.name("first_name"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.contact.last_name</code>.
     */
    public final TableField<ContactRecord, String> LAST_NAME = createField(DSL.name("last_name"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.contact.phone</code>.
     */
    public final TableField<ContactRecord, String> PHONE = createField(DSL.name("phone"), org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.contact.subscribe</code>.
     */
    public final TableField<ContactRecord, Boolean> SUBSCRIBE = createField(DSL.name("subscribe"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.contact.created_on</code>.
     */
    public final TableField<ContactRecord, Timestamp> CREATED_ON = createField(DSL.name("created_on"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>public.contact.updated_on</code>.
     */
    public final TableField<ContactRecord, Timestamp> UPDATED_ON = createField(DSL.name("updated_on"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * Create a <code>public.contact</code> table reference
     */
    public Contact() {
        this(DSL.name("contact"), null);
    }

    /**
     * Create an aliased <code>public.contact</code> table reference
     */
    public Contact(String alias) {
        this(DSL.name(alias), CONTACT);
    }

    /**
     * Create an aliased <code>public.contact</code> table reference
     */
    public Contact(Name alias) {
        this(alias, CONTACT);
    }

    private Contact(Name alias, Table<ContactRecord> aliased) {
        this(alias, aliased, null);
    }

    private Contact(Name alias, Table<ContactRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Contact(Table<O> child, ForeignKey<O, ContactRecord> key) {
        super(child, key, CONTACT);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CONTACT_EMAIL_KEY, Indexes.CONTACT_PKEY);
    }

    @Override
    public Identity<ContactRecord, Long> getIdentity() {
        return Keys.IDENTITY_CONTACT;
    }

    @Override
    public UniqueKey<ContactRecord> getPrimaryKey() {
        return Keys.CONTACT_PKEY;
    }

    @Override
    public List<UniqueKey<ContactRecord>> getKeys() {
        return Arrays.<UniqueKey<ContactRecord>>asList(Keys.CONTACT_PKEY, Keys.CONTACT_EMAIL_KEY);
    }

    @Override
    public Contact as(String alias) {
        return new Contact(DSL.name(alias), this);
    }

    @Override
    public Contact as(Name alias) {
        return new Contact(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Contact rename(String name) {
        return new Contact(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Contact rename(Name name) {
        return new Contact(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, String, String, String, String, Boolean, Timestamp, Timestamp> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
