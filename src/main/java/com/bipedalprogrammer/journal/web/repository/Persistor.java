package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Author;
import com.bipedalprogrammer.journal.web.model.Document;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

import static com.bipedalprogrammer.journal.web.model.Author.AUTHOR_DEFAULT_ID;
import static com.bipedalprogrammer.journal.web.repository.OrientStore.*;
import static com.bipedalprogrammer.journal.web.repository.OrientStore.AUTHOR_EMAIL;

@Component
public class Persistor {
    private OrientStore orientStore;

    private Logger logger = LoggerFactory.getLogger(Persistor.class);

    private static final String FIND_DOCUMENTS_BY_ID = "SELECT FROM Documents WHERE documentId = ?";
    private static final String FIND_AUTHOR_BY_ID = "SELECT FROM Authors where authorId = ?";
    private static final String FIND_BY_EMAIL_ADDRESS = "SELECT FROM Authors WHERE email = ?";
    private static final String FIND_BY_AUTHOR_ID = "SELECT FROM Authors WHERE authorId = ?";
    private static final String FIND_BY_NAME = "SELECT FROM Authors WHERE firstName = ? AND lastName = ?";
    private static final String FIND_ALL = "SELECT FROM Authors";

    @Autowired
    public Persistor(OrientStore orientStore) {
        this.orientStore = orientStore;
    }

    public Author newAuthor(String firstName, String lastName, String emailAddress) {
        try (ODatabaseSession db = orientStore.getSession()) {
            OVertex vertex = createAuthor(db, firstName, lastName, emailAddress);
            Author author = new Author(firstName, lastName, emailAddress);
            author.setAuthorId(vertex.getProperty(AUTHOR_ID));
            return author;
        } catch (Exception ex) {
            logger.info("Cannot create author.", ex);
        }
        return null;
    }

    public Author update(Author author) {

        try (ODatabaseSession db = orientStore.getSession()) {
            OVertex vertex = loadAuthor(db, author.getAuthorId());
            vertex.setProperty(AUTHOR_FIRST_NAME, author.getFirstName());
            vertex.setProperty(AUTHOR_LAST_NAME, author.getLastName());
            vertex.setProperty(AUTHOR_EMAIL, author.getEmailAddress());
            db.save(vertex);
        } catch (Exception ex) {
            logger.info("Unable to save author.", ex);
        }

        return author;

    }

    public Author findByEmailAddress(String emailAddress) {
        try (ODatabaseSession db = orientStore.getSession()) {
            OResultSet resultSet = db.query(FIND_BY_EMAIL_ADDRESS, emailAddress);
            Author author = new Author();
            if (resultSet.hasNext()) {
                OResult result = resultSet.next();
                result.getVertex().ifPresent(v -> {
                    authorFromVertex(author, v);
                });
            }
            resultSet.close();
            return author;
        } catch (Exception ex) {
            logger.info("Unable to save author.", ex);
        }

        return null;
    }

    public List<Author> findAuthorByName(String firstName, String lastName) {
        List<Author> authors = new ArrayList<>();
        try (ODatabaseSession db = orientStore.getSession()) {
            try (OResultSet rs = db.query(FIND_BY_NAME, firstName, lastName)) {
                while (rs.hasNext()) {
                    rs.next().getVertex().ifPresent(v -> {
                        Author author = new Author();
                        authorFromVertex(author, v);
                        authors.add(author);
                    });
                }
            }
        }
        return authors;
    }

    public List<Author> findAllAuthors() {
        List<Author> authors = new ArrayList<Author>();
        try (ODatabaseSession db = orientStore.getSession()) {
            for (ODocument doc : db.browseClass(AUTHOR_SCHEMA)) {
                doc.asVertex().ifPresent(v -> {
                    Author author = new Author();
                    authorFromVertex(author, v);
                    authors.add(author);
                });
            }
        }
        return authors;
    }

    public boolean delete(Author author) {
        try (ODatabaseSession db = orientStore.getSession()) {
            OVertex vertex = loadAuthor(db, author.getAuthorId());
            db.delete(vertex);
        }
        return true;
    }

    public Document newDocument(Document document) {
        try (ODatabaseSession db = orientStore.getSession()) {
            Long documentId = db.getMetadata().getSequenceLibrary().getSequence(DOCUMENT_SEQUENCE).next();
            OVertex vertex = db.newVertex(DOCUMENT_SCHEMA);
            vertex.setProperty(DOCUMENT_ID, documentId);
            setVertexProperties(vertex, document);
            db.save(vertex);
            document.setDocumentId(documentId);
            Set<OVertex> authors = resolveAuthors(db, vertex, document.getAuthors());

            return document;
        }
    }

    public Document save(Document document) {
        try (ODatabaseSession db = orientStore.getSession()) {
            AtomicReference<OVertex> vertexRef = new AtomicReference<>();
            try (OResultSet rs = db.query(FIND_DOCUMENTS_BY_ID, document.getDocumentId())) {
                if (rs.hasNext()) {
                    rs.next().getVertex().ifPresent(v -> {
                        setVertexProperties(v, document);
                        vertexRef.set(db.save(v));
                    });
                } else {
                    logger.warn("Document id " + document.getDocumentId() + "not found. Save failed.");
                    return null;
                }
            }
            // TODO See if we have added any authors.

            Set<OVertex> authors = resolveAuthors(db, vertexRef.get(), document.getAuthors());
            return document;
        }
    }

    public Document findByDocumentId(Long documentId, boolean deepFind) {
        try (ODatabaseSession db = orientStore.getSession()) {
            OResultSet rs = db.query(FIND_DOCUMENTS_BY_ID, documentId);
            AtomicReference<OVertex> found = null;
            if (rs.hasNext()) {
                rs.next().getVertex().ifPresent(v -> found.set(v));
            }
            rs.close();
            Document document = documentFromVertex(found.get());
            if (deepFind) {
                loadDocumentAuthors(db, found.get(), document);
            }
            return document;
        }
    }

    public List<Document> findAllDocuments() {

        try (ODatabaseSession db = orientStore.getSession()) {
            List<Document> documents = new ArrayList<>();
            for (ODocument doc : db.browseClass("DOCUMENT_SCHEMA")) {
                doc.asVertex().ifPresent(v -> {
                    documents.add(documentFromVertex(v));
                });
            }
            return documents;
        }
    }

    private Set<OVertex> getVerticiesByEmailAddress(Set<String> emails) {
        try (ODatabaseSession db = orientStore.getSession()) {
            OResultSet resultSet = db.query("SELECT FROM Authors WHERE email in ?", emails);
            Set<OVertex> found = new HashSet<>();
            while (resultSet.hasNext()) {
                OResult result = resultSet.next();
                result.getVertex().ifPresent(v -> {
                    found.add(v);
                });
            }
            resultSet.close();
            return found;
        }
    }

    private OVertex createAuthor(ODatabaseSession db, String firstName, String lastName, String emailAddress) {
        Long authorId = db.getMetadata().getSequenceLibrary().getSequence(AUTHOR_SEQUENCE).next();
        OVertex vertex = db.newVertex(AUTHOR_SCHEMA);
        vertex.setProperty(AUTHOR_ID, authorId);
        vertex.setProperty(AUTHOR_FIRST_NAME, firstName);
        vertex.setProperty(AUTHOR_LAST_NAME, lastName);
        vertex.setProperty(AUTHOR_EMAIL, emailAddress);
        OVertex saved = db.save(vertex);
        return saved;
    }

    private void authorFromVertex(Author author, OVertex v) {
        author.setAuthorId(v.getProperty(AUTHOR_ID));
        author.setFirstName(v.getProperty(AUTHOR_FIRST_NAME));
        author.setLastName(v.getProperty(AUTHOR_LAST_NAME));
        author.setEmailAddress(v.getProperty(AUTHOR_EMAIL));
    }

    private OVertex loadAuthor(ODatabaseSession db, long authorId) {
        try (OResultSet vertices = db.query(FIND_AUTHOR_BY_ID, authorId)){
            Optional<OVertex> result = vertices.next().getVertex();
            if (result.isPresent()) return result.get();
            else return null;
        }
    }

    private void setVertexProperties(OVertex v, Document document) {
        v.setProperty(DOCUMENT_TITLE, document.getTitle());
        v.setProperty(DOCUMENT_VERSION, document.getRevision());
        v.setProperty(DOCUMENT_REVISION_DATE, document.getRevisionDate());
        v.setProperty(DOCUMENT_BODY, document.getBody());
    }

    private Document documentFromVertex(OVertex v) {
        Document d = new Document();
        d.setDocumentId(v.getProperty(DOCUMENT_ID));
        d.setTitle(v.getProperty(DOCUMENT_TITLE));
        d.setRevision(v.getProperty(DOCUMENT_VERSION));
        d.setRevisionDate(v.getProperty(DOCUMENT_REVISION_DATE));
        d.setBody(v.getProperty(DOCUMENT_BODY));
        return d;
    }

    private Set<OVertex> resolveAuthors(ODatabaseSession db, OVertex document, Set<Author> authors) {
        Set<OVertex> vertices = new HashSet<>();
        Iterable<OVertex> existing = document.getVertices(ODirection.OUT, DOCUMENT_AUTHOR_SCHEMA);
        for (Author author : authors) {
            if (author.getAuthorId() != AUTHOR_DEFAULT_ID) {
                OVertex vertex = loadAuthor(db, author.getAuthorId());
                if (vertex != null) {
                    vertices.add(vertex);
                } else {
                    logger.warn("Document contained an invalid aothorId [" + author.getAuthorId() + "].");
                }
                if (!documentAuthorEdgeExists(existing, vertex)) {
                    addDocumentAuthor(db, document, vertex);
                }
            } else {
                OVertex vertex = createAuthor(db, author.getFirstName(), author.getLastName(), author.getEmailAddress());
                if (vertex != null) {
                    vertices.add(vertex);
                    author.setAuthorId(vertex.getProperty(AUTHOR_ID));
                } else {
                    logger.warn("Cannot add author to store.");
                }
                addDocumentAuthor(db, document, vertex);
            }
        }
        return  vertices;
    }

    private boolean documentAuthorEdgeExists(Iterable<OVertex> existing, OVertex author) {
        return StreamSupport.stream(existing.spliterator(), true)
                .anyMatch(v -> v.getProperty(AUTHOR_ID) == author.getProperty(AUTHOR_ID));
    }

    private OEdge addDocumentAuthor(ODatabaseSession db, OVertex document, OVertex author) {
        OEdge documentAuthor = db.newEdge(document, author, DOCUMENT_AUTHOR_SCHEMA);
        if (documentAuthor == null) {
            logger.error("Unable to create edge from document " + document.getProperty(DOCUMENT_ID)
                    + "to author " + author.getProperty(AUTHOR_ID) + ".");
        }
        ODocument saved = db.save(documentAuthor);
        if (saved == null) {
            logger.error("Edge was not saved to database.");
        }
        if (saved.asEdge().isPresent()) return saved.asEdge().get();
        else return null;

    }

    private void loadDocumentAuthors(ODatabaseSession db, OVertex from, Document document) {
        Iterable<OVertex> existing = from.getVertices(ODirection.OUT, DOCUMENT_AUTHOR_SCHEMA);
        existing.forEach( v -> {
            Author author = new Author();
            authorFromVertex(author, v);
            document.getAuthors().add(author);
        });
    }
}
