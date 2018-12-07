package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Author;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bipedalprogrammer.journal.web.repository.OrientStore.*;

@Component
public class AuthorRepository {
    private OrientStore orientStore;

    private Logger logger = LoggerFactory.getLogger(AuthorRepository.class);

    private static final String FIND_BY_EMAIL_ADDRESS = "SELECT FROM Authors WHERE email = ?";
    private static final String FIND_BY_AUTHOR_ID = "SELECT FROM Authors WHERE authorId = ?";
    private static final String FIND_BY_NAME = "SELECT FROM Authors WHERE firstName = ? AND lastName = ?";
    private static final String FIND_ALL = "SELECT FROM Authors";

    @Autowired
    public AuthorRepository(OrientStore orientStore) {
        this.orientStore = orientStore;
    }

    public Author newAuthor(String firstName, String lastName, String emailAddress) {
        try (ODatabaseSession db = orientStore.getSession()) {
            Long authorId = db.getMetadata().getSequenceLibrary().getSequence(AUTHOR_SEQUENCE).next();
            OVertex vertex = db.newVertex(AUTHOR_SCHEMA);
            vertex.setProperty(AUTHOR_ID, authorId);
            vertex.setProperty(AUTHOR_FIRST_NAME, firstName);
            vertex.setProperty(AUTHOR_LAST_NAME, lastName);
            vertex.setProperty(AUTHOR_EMAIL, emailAddress);
            OVertex saved = db.save(vertex);
            Author author = new Author(firstName, lastName, emailAddress);
            author.setAuthorId(authorId);
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
            return author;
        } catch (Exception ex) {
            logger.info("Unable to save author.", ex);
        }

        return null;
    }

    public List<Author> findAll() {
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

    private void authorFromVertex(Author author, OVertex v) {
        author.setAuthorId(v.getProperty(AUTHOR_ID));
        author.setFirstName(v.getProperty(AUTHOR_FIRST_NAME));
        author.setLastName(v.getProperty(AUTHOR_LAST_NAME));
        author.setEmailAddress(v.getProperty(AUTHOR_EMAIL));
    }

    private OVertex loadAuthor(ODatabaseSession db, long authorId) {
        OResultSet vertices = db.query(FIND_BY_AUTHOR_ID, authorId);
        Optional<OVertex> result = vertices.next().getVertex();
        if (result.isPresent()) return result.get();
        else return null;
    }
}
