/*
 * This file is generated by jOOQ.
*/
package meta.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;


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
public class Contact implements Serializable {

    private static final long serialVersionUID = -568777031;

    private final Long      id;
    private final String    email;
    private final String    firstName;
    private final String    lastName;
    private final String    phone;
    private final Boolean   subscribe;
    private final Timestamp createdOn;
    private final Timestamp updatedOn;

    public Contact(Contact value) {
        this.id = value.id;
        this.email = value.email;
        this.firstName = value.firstName;
        this.lastName = value.lastName;
        this.phone = value.phone;
        this.subscribe = value.subscribe;
        this.createdOn = value.createdOn;
        this.updatedOn = value.updatedOn;
    }

    public Contact(
        Long      id,
        String    email,
        String    firstName,
        String    lastName,
        String    phone,
        Boolean   subscribe,
        Timestamp createdOn,
        Timestamp updatedOn
    ) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.subscribe = subscribe;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPhone() {
        return this.phone;
    }

    public Boolean getSubscribe() {
        return this.subscribe;
    }

    public Timestamp getCreatedOn() {
        return this.createdOn;
    }

    public Timestamp getUpdatedOn() {
        return this.updatedOn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Contact (");

        sb.append(id);
        sb.append(", ").append(email);
        sb.append(", ").append(firstName);
        sb.append(", ").append(lastName);
        sb.append(", ").append(phone);
        sb.append(", ").append(subscribe);
        sb.append(", ").append(createdOn);
        sb.append(", ").append(updatedOn);

        sb.append(")");
        return sb.toString();
    }
}
