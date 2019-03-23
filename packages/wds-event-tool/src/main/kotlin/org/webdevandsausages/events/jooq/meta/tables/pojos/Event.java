/*
 * This file is generated by jOOQ.
*/
package meta.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;

import meta.enums.EventStatus;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Event implements Serializable {

    private static final long serialVersionUID = -722875272;

    private final Long        id;
    private final String      name;
    private final String      sponsor;
    private final String      contact;
    private final Timestamp   date;
    private final String      details;
    private final String      location;
    private final EventStatus status;
    private final Integer     maxParticipants;
    private final Timestamp   registrationOpens;
    private final Timestamp   createdOn;
    private final Timestamp   updatedOn;
    private final Integer     volume;
    private final String      sponsorLink;

    public Event(Event value) {
        this.id = value.id;
        this.name = value.name;
        this.sponsor = value.sponsor;
        this.contact = value.contact;
        this.date = value.date;
        this.details = value.details;
        this.location = value.location;
        this.status = value.status;
        this.maxParticipants = value.maxParticipants;
        this.registrationOpens = value.registrationOpens;
        this.createdOn = value.createdOn;
        this.updatedOn = value.updatedOn;
        this.volume = value.volume;
        this.sponsorLink = value.sponsorLink;
    }

    public Event(
        Long        id,
        String      name,
        String      sponsor,
        String      contact,
        Timestamp   date,
        String      details,
        String      location,
        EventStatus status,
        Integer     maxParticipants,
        Timestamp   registrationOpens,
        Timestamp   createdOn,
        Timestamp   updatedOn,
        Integer     volume,
        String      sponsorLink
    ) {
        this.id = id;
        this.name = name;
        this.sponsor = sponsor;
        this.contact = contact;
        this.date = date;
        this.details = details;
        this.location = location;
        this.status = status;
        this.maxParticipants = maxParticipants;
        this.registrationOpens = registrationOpens;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.volume = volume;
        this.sponsorLink = sponsorLink;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getSponsor() {
        return this.sponsor;
    }

    public String getContact() {
        return this.contact;
    }

    public Timestamp getDate() {
        return this.date;
    }

    public String getDetails() {
        return this.details;
    }

    public String getLocation() {
        return this.location;
    }

    public EventStatus getStatus() {
        return this.status;
    }

    public Integer getMaxParticipants() {
        return this.maxParticipants;
    }

    public Timestamp getRegistrationOpens() {
        return this.registrationOpens;
    }

    public Timestamp getCreatedOn() {
        return this.createdOn;
    }

    public Timestamp getUpdatedOn() {
        return this.updatedOn;
    }

    public Integer getVolume() {
        return this.volume;
    }

    public String getSponsorLink() {
        return this.sponsorLink;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Event (");

        sb.append(id);
        sb.append(", ").append(name);
        sb.append(", ").append(sponsor);
        sb.append(", ").append(contact);
        sb.append(", ").append(date);
        sb.append(", ").append(details);
        sb.append(", ").append(location);
        sb.append(", ").append(status);
        sb.append(", ").append(maxParticipants);
        sb.append(", ").append(registrationOpens);
        sb.append(", ").append(createdOn);
        sb.append(", ").append(updatedOn);
        sb.append(", ").append(volume);
        sb.append(", ").append(sponsorLink);

        sb.append(")");
        return sb.toString();
    }
}
