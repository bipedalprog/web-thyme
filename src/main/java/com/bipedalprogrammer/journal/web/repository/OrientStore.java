package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.config.DatabaseConfiguration;
import com.bipedalprogrammer.journal.web.config.OrientConfiguration;
import com.bipedalprogrammer.journal.web.model.Author;
import com.bipedalprogrammer.journal.web.model.Document;
import com.bipedalprogrammer.journal.web.model.Notebook;
import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.object.db.ODatabaseObjectPool;
import com.orientechnologies.orient.object.db.OrientDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class OrientStore {
    public static final String DATABASE_NAME = "notebooks";
    private OrientDBObject orientDB;
    private DatabaseConfiguration config;
    private ODatabaseObjectPool pool;

    public static String AUTHOR_SCHEMA = "Authors";
    public static String AUTHOR_ID = "authorId";
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
    public OrientStore(DatabaseConfiguration config) {
        this.config = config;
        openOrCreateDataStore();
    }

    @PreDestroy
    public void releaseResources() {
        this.pool.close();
        this.orientDB.close();
    }

    public ODatabaseObject getSession() {
        return pool.acquire();
    }

    private void openOrCreateDataStore() {
        this.orientDB = new OrientDBObject(config.getConnectionString(), OrientDBConfig.defaultConfig());
        if (!orientDB.exists(DATABASE_NAME)) {
            createDataStore();
        }
        this.pool = new ODatabaseObjectPool(this.orientDB, "notebooks", config.getUser(), config.getPassword());

        ODatabaseObject db = pool.acquire();

        //db.getEntityManager().registerEntityClasses("com.bipedalprogrammer.journal.web.model");
        db.getEntityManager().registerEntityClass(Author.class);
        db.getEntityManager().registerEntityClass(Document.class);
        db.getEntityManager().registerEntityClass(Notebook.class);


        db.close();
    }

    private void createDataStore() {
        orientDB.create(DATABASE_NAME, ODatabaseType.PLOCAL);
        ODatabaseObject db = orientDB.open(DATABASE_NAME, config.getUser(), config.getPassword());
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
        myClass.createProperty(AUTHOR_ID, OType.STRING).setNotNull(true);
        myClass.createIndex(AUTHOR_SCHEMA+AUTHOR_ID, OClass.INDEX_TYPE.UNIQUE, AUTHOR_ID);
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
