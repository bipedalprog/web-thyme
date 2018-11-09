package com.bipedalprogrammer.journal.web.model;

import com.orientechnologies.orient.core.id.ORID;

import javax.persistence.Id;
import javax.persistence.Version;

public class Author {
    @Id
    private String authorId;
    @Version
    private Long version;

    private String firstName;
    private String lastName;
    private String emailAddress;

    public Author() {}

    public Author(String firstName, String lastName, String emailAddress) {
        this.authorId = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }

    public String getAuthorId() { return authorId; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
