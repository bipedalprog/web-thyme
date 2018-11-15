package com.bipedalprogrammer.journal.web.model;

import javax.persistence.Id;
import javax.persistence.Version;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Document {
    @Id
    private String documentId;
    @Version
    private Long version;

    private String title;
    private List<Author> authors;
    private String revision;
    private Date revisionDate;
    private String body;
    private Set<String> tags;

    public Document() {}

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

}
