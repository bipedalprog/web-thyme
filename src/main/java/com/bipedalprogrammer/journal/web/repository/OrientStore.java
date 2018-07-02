package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.config.OrientConfiguration;
import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class OrientStore {
    public static final String DATABASE_NAME = "notebooks";
    private OrientDB orientDB;
    private OrientConfiguration config;
    private ODatabasePool pool;

    public static String AUTHOR_SCHEMA = "Authors";
    public static String AUTHOR_FIRST_NAME = "firstName";
    public static String AUTHOR_LAST_NAME = "lastName";
    public static String AUTHOR_EMAIL = "email";

    public static String DOCUMENT_SCHEMA = "Documents";
    public static String DOCUMENT_ID = "documentId";
    public static String DOCUMENT_TITLE = "title";
    public static String DOCUMENT_AUTHORS = "authors";
    public static String DOCUMENT_VERSION = "version";
    public static String DOCUMENT_REVISION_DATE = "revisionDate";
    public static String DOCUMENT_BODY = "body";
    public static String DOCUMENT_TAGS = "tags";

    public static String NOTEBOOK_SCHEMA = "Notebooks";
    public static String NOTEBOOK_ID = "notebookId";
    public static String NOTEBOOK_TITLE = "title";
    public static String NOTEBOOK_CREATED = "created";
    public static String NOTEBOOK_UPDATED = "updated";
    public static String NOTEBOOK_AUTHORS = "authors";
    public static String NOTEBOOK_BASEPATH = "basePath";

    @Autowired
    public OrientStore(OrientConfiguration config) {
        this.config = config;
        openOrCreateDataStore();
    }

    @PreDestroy
    public void releaseResources() {
        this.pool.close();
        this.orientDB.close();
    }

    private void openOrCreateDataStore() {
        this.orientDB = new OrientDB(config.getConnectionString(), OrientDBConfig.defaultConfig());
        if (!orientDB.exists(DATABASE_NAME)) {
            createDataStore();
        }
        this.pool = new ODatabasePool(this.orientDB, "notebooks", config.getUser(), config.getPassword());

        ODatabaseSession db = pool.acquire();
    }

    private void createDataStore() {
        orientDB.create(DATABASE_NAME, ODatabaseType.PLOCAL);
        ODatabaseSession db = orientDB.open(DATABASE_NAME, config.getUser(), config.getPassword());
        try {
            OSchema schema = db.getMetadata().getSchema();
            if (!schema.existsClass(AUTHOR_SCHEMA)) createAuthorSchema(schema);
            if (!schema.existsClass(DOCUMENT_SCHEMA)) createDocumentSchema(schema);
            if (!schema.existsClass(NOTEBOOK_SCHEMA)) createNotebookSchema(schema);
        } finally {
            db.close();
        }
    }

    private void createAuthorSchema(OSchema schema) {
        OClass myClass = schema.createClass(AUTHOR_SCHEMA);
        myClass.createProperty(AUTHOR_FIRST_NAME, OType.STRING).setNotNull(true);
        myClass.createIndex(AUTHOR_SCHEMA+AUTHOR_FIRST_NAME, OClass.INDEX_TYPE.NOTUNIQUE, AUTHOR_FIRST_NAME);
        myClass.createProperty(AUTHOR_LAST_NAME, OType.STRING).setNotNull(true);
        myClass.createIndex(AUTHOR_SCHEMA+AUTHOR_LAST_NAME, OClass.INDEX_TYPE.NOTUNIQUE, AUTHOR_LAST_NAME);
        myClass.createProperty(AUTHOR_EMAIL, OType.STRING).setNotNull(false);
        myClass.createIndex(AUTHOR_SCHEMA+AUTHOR_EMAIL, OClass.INDEX_TYPE.NOTUNIQUE, AUTHOR_EMAIL);
    }

    private void createDocumentSchema(OSchema schema) {
        OClass myClass = schema.createClass(DOCUMENT_SCHEMA);
        myClass.createProperty(DOCUMENT_ID, OType.STRING).setNotNull(true);
        myClass.createIndex(DOCUMENT_SCHEMA+DOCUMENT_ID, OClass.INDEX_TYPE.UNIQUE, DOCUMENT_ID);
        myClass.createProperty(DOCUMENT_TITLE, OType.STRING).setNotNull(true);
        myClass.createIndex(DOCUMENT_SCHEMA+DOCUMENT_TITLE, OClass.INDEX_TYPE.NOTUNIQUE, DOCUMENT_TITLE);
        myClass.createProperty(DOCUMENT_AUTHORS, OType.LINKSET).setNotNull(true);
        myClass.createIndex(DOCUMENT_SCHEMA+DOCUMENT_AUTHORS, OClass.INDEX_TYPE.NOTUNIQUE, DOCUMENT_AUTHORS);
        myClass.createProperty(DOCUMENT_VERSION, OType.STRING).setNotNull(true);
        myClass.createProperty(DOCUMENT_REVISION_DATE, OType.DATE).setNotNull(false);
        myClass.createProperty(DOCUMENT_BODY, OType.STRING).setNotNull(false);
        myClass.createProperty(DOCUMENT_TAGS, OType.LINKSET).setNotNull(false);
    }

    private void createNotebookSchema(OSchema schema) {
        OClass myClass = schema.createClass(NOTEBOOK_SCHEMA);
        myClass.createProperty(NOTEBOOK_ID, OType.STRING).setNotNull(true);
        myClass.createIndex(NOTEBOOK_SCHEMA+NOTEBOOK_ID, OClass.INDEX_TYPE.UNIQUE,NOTEBOOK_ID);
        myClass.createProperty(NOTEBOOK_TITLE, OType.STRING).setNotNull(true);
        myClass.createIndex(NOTEBOOK_SCHEMA+NOTEBOOK_TITLE, OClass.INDEX_TYPE.UNIQUE, NOTEBOOK_TITLE);
        myClass.createProperty(NOTEBOOK_CREATED, OType.DATE).setNotNull(true);
        myClass.createProperty(NOTEBOOK_UPDATED, OType.DATE).setNotNull(true);
        myClass.createProperty(NOTEBOOK_AUTHORS, OType.LINKSET).setNotNull(true);
        myClass.createProperty(NOTEBOOK_BASEPATH, OType.STRING).setNotNull(true);
    }
}
