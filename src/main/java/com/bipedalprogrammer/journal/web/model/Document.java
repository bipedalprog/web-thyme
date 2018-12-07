package com.bipedalprogrammer.journal.web.model;

import com.orientechnologies.orient.core.record.OVertex;

import javax.persistence.Id;
import javax.persistence.Version;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.bipedalprogrammer.journal.web.repository.OrientStore.*;

public class Document {
    private OVertex vertex;

    public Document() {}

    public Document(OVertex vertex) {
        this.vertex = vertex;
    }

    public String getDocumentId() {
        return vertex.getProperty(DOCUMENT_ID);
    }

    public void setDocumentId(String documentId) {
        vertex.setProperty(DOCUMENT_ID, documentId);
    }

    public String getTitle() {
        return vertex.getProperty(DOCUMENT_TITLE);
    }

    public void setTitle(String title) {
        vertex.setProperty(DOCUMENT_TITLE, title);
    }

    public String getRevision() {
        return vertex.getProperty(DOCUMENT_VERSION);
    }

    public void setRevision(String revision) {
        vertex.setProperty(DOCUMENT_VERSION, revision);
    }

    public Date getRevisionDate() {
        return vertex.getProperty(DOCUMENT_REVISION_DATE);
    }

    public void setRevisionDate(Date revisionDate) {
        vertex.setProperty(DOCUMENT_REVISION_DATE, revisionDate);
    }

    public String getBody() {
        return vertex.getProperty(DOCUMENT_BODY);
    }

    public void setBody(String body) {
        vertex.setProperty(DOCUMENT_BODY, body);
    }

}
