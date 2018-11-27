package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Document;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentRepository {
    private OrientStore orientStore;

    @Autowired
    public DocumentRepository(OrientStore orientStore) {
        this.orientStore = orientStore;
    }

    public Document newDocument() {
        try (ODatabaseObject db = orientStore.getSession()) {
            Document document = db.newInstance(Document.class);
            return db.detach(document, true);
        }
    }

    public Document save(Document document) {
        try (ODatabaseObject db = orientStore.getSession()) {
            Document updated = db.save(document);
            return db.detach(updated, true);
        }
    }


}
