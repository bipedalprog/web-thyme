package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.config.DatabaseConfiguration;
import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.sequence.OSequence;
import com.orientechnologies.orient.core.metadata.sequence.OSequenceLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Set;

@Component
public class OrientStore {
    public static final String DATABASE_NAME = "notebooks";
    private OrientDB orientDB;
    private DatabaseConfiguration config;
    private ODatabasePool pool;

    public static String AUTHOR_SEQUENCE = "AuthorSequence";
    public static String DOCUMENT_SEQUENCE = "DocumentSequence";
    public static String NOTEBOOK_SEQUENCE = "NotebookSequence";

    public static String AUTHOR_SCHEMA = "Authors";
    public static String AUTHOR_ID = "authorId";
    public static String AUTHOR_FIRST_NAME = "firstName";
    public static String AUTHOR_LAST_NAME = "lastName";
    public static String AUTHOR_EMAIL = "email";

    public static String DOCUMENT_SCHEMA = "Documents";
    public static String DOCUMENT_ID = "documentId";
    public static String DOCUMENT_TITLE = "title";
    public static String DOCUMENT_VERSION = "version";
    public static String DOCUMENT_REVISION_DATE = "revisionDate";
    public static String DOCUMENT_BODY = "body";

    public static String NOTEBOOK_SCHEMA = "Notebooks";
    public static String NOTEBOOK_ID = "notebookId";
    public static String NOTEBOOK_TITLE = "title";
    public static String NOTEBOOK_CREATED = "created";
    public static String NOTEBOOK_UPDATED = "updated";
    public static String NOTEBOOK_BASEPATH = "basePath";

    public static String USER_USERNAME = "userName";
    public static String USER_PASSWORD = "password";
    public static String USER_EMAIL = "email";
    public static String USER_ENABLED = "enabled";
    public static String USER_ROLES = "roles";

    public static String DOCUMENT_AUTHOR_SCHEMA = "DocumentAuthors";
    public static String NOTEBOOK_AUTHOR_SCHEMA = "NotebookAuthors";
    public static String NOTEBOOK_DOCUMENT_SCHEMA = "NotebookDocuments";
    public static String USER_SCHEMA = "Users";

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

    ODatabaseSession getSession() {
        return pool.acquire();
    }

    private void openOrCreateDataStore() {
        this.orientDB = new OrientDB(config.getConnectionString(), OrientDBConfig.defaultConfig());
        if (!orientDB.exists(DATABASE_NAME)) {
            createDataStore();
        }
        this.pool = new ODatabasePool(orientDB, DATABASE_NAME, config.getUser(), config.getPassword());

        ODatabase db = pool.acquire();

        db.close();
    }

    private void createDataStore() {
        ODatabaseType type = config.getConnectionString().startsWith("memory") ?
                ODatabaseType.MEMORY : ODatabaseType.PLOCAL;
        orientDB.create(DATABASE_NAME, type);
        try (ODatabaseSession db = orientDB.open(DATABASE_NAME, config.getUser(), config.getPassword())) {
            // Create the sequences used in individual verticies.
            Set<String> sequences = db.getMetadata().getSequenceLibrary().getSequenceNames();
            if (!sequences.contains(AUTHOR_SEQUENCE)) createSequence(db, AUTHOR_SEQUENCE);
            if (!sequences.contains(DOCUMENT_SEQUENCE)) createSequence(db, DOCUMENT_SEQUENCE);
            if (!sequences.contains(NOTEBOOK_SEQUENCE)) createSequence(db, NOTEBOOK_SEQUENCE);
            OSchema schema = db.getMetadata().getSchema();
            // Create the vertex classes.
            if (!schema.existsClass(AUTHOR_SCHEMA)) createAuthorSchema(db);
            if (!schema.existsClass(DOCUMENT_SCHEMA)) createDocumentSchema(db);
            if (!schema.existsClass(NOTEBOOK_SCHEMA)) createNotebookSchema(db);
            if (!schema.existsClass(USER_SCHEMA)) createUserSchema(db);
            // Create the edge classes.
            if (!schema.existsClass(DOCUMENT_AUTHOR_SCHEMA)) createEdgeSchema(db, DOCUMENT_AUTHOR_SCHEMA);
            if (!schema.existsClass(NOTEBOOK_AUTHOR_SCHEMA)) createEdgeSchema(db, NOTEBOOK_AUTHOR_SCHEMA);
            if (!schema.existsClass(NOTEBOOK_DOCUMENT_SCHEMA)) createEdgeSchema(db, NOTEBOOK_DOCUMENT_SCHEMA);
        }
    }

    private void createSequence(ODatabaseSession db, String name) {
        OSequenceLibrary sequenceLibrary = db.getMetadata().getSequenceLibrary();
        sequenceLibrary.createSequence(name, OSequence.SEQUENCE_TYPE.ORDERED, new OSequence.CreateParams()
                .setStart(1L).setIncrement(1));
    }

    private void createAuthorSchema(ODatabaseSession db) {
        OClass myClass = db.createVertexClass(AUTHOR_SCHEMA);
        createIndexedPropery(myClass, true, AUTHOR_SCHEMA, AUTHOR_ID, OType.LONG);
        createIndexedPropery(myClass, false, AUTHOR_SCHEMA, AUTHOR_FIRST_NAME, OType.STRING);
        createIndexedPropery(myClass, false, AUTHOR_SCHEMA, AUTHOR_LAST_NAME, OType.STRING);
        createIndexedPropery(myClass, false, AUTHOR_SCHEMA, AUTHOR_EMAIL, OType.STRING);
    }

    private void createDocumentSchema(ODatabaseSession db) {
        OClass myClass = db.createVertexClass(DOCUMENT_SCHEMA);
        createIndexedPropery(myClass, true, DOCUMENT_SCHEMA, DOCUMENT_ID, OType.LONG);
        createIndexedPropery(myClass, true, DOCUMENT_SCHEMA, DOCUMENT_TITLE, OType.STRING);
        myClass.createProperty(DOCUMENT_VERSION, OType.STRING).setNotNull(true);
        myClass.createProperty(DOCUMENT_REVISION_DATE, OType.DATE).setNotNull(false);
        myClass.createProperty(DOCUMENT_BODY, OType.STRING).setNotNull(false);
    }

    private void createNotebookSchema(ODatabaseSession db) {
        OClass myClass = db.createVertexClass(NOTEBOOK_SCHEMA);
        createIndexedPropery(myClass, true, NOTEBOOK_SCHEMA, NOTEBOOK_ID, OType.LONG);
        createIndexedPropery(myClass, true, NOTEBOOK_SCHEMA, NOTEBOOK_TITLE, OType.STRING);
        myClass.createProperty(NOTEBOOK_CREATED, OType.DATE).setNotNull(true);
        myClass.createProperty(NOTEBOOK_UPDATED, OType.DATE).setNotNull(true);
        myClass.createProperty(NOTEBOOK_BASEPATH, OType.STRING).setNotNull(true);
    }

    private void createUserSchema(ODatabaseSession db) {
        OClass myClass = db.createVertexClass(USER_SCHEMA);
        createIndexedPropery(myClass, true, USER_SCHEMA, USER_EMAIL, OType.STRING);
        createIndexedPropery(myClass, false, USER_SCHEMA, USER_PASSWORD, OType.STRING);
        myClass.createProperty(USER_ENABLED, OType.BOOLEAN);
        myClass.createProperty(USER_ROLES, OType.STRING).setNotNull(true);
    }

    private void createEdgeSchema(ODatabaseSession db, String edgeClass) {
        OClass myClass = db.createEdgeClass(edgeClass);
        assert myClass != null;
    }

    private void createIndexedPropery(OClass myClass, boolean unique, String schemaName, String propertyName, OType type) {
        myClass.createProperty(propertyName, type).setNotNull(unique);
        myClass.createIndex(schemaName + propertyName,
                unique ? OClass.INDEX_TYPE.UNIQUE : OClass.INDEX_TYPE.NOTUNIQUE, propertyName);
    }

}
