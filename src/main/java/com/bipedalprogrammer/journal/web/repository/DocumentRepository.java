package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Document;
import com.orientechnologies.orient.core.db.ODatabaseSession;
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
        try (ODatabaseSession db = orientStore.getSession()) {
        }
        return null;
    }

    public Document save(Document document) {
        try (ODatabaseSession db = orientStore.getSession()) {
        }
        return null;
    }


}
