package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Author;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorRepository {
    private OrientStore orientStore;

    private Logger logger = LoggerFactory.getLogger(AuthorRepository.class);
    private OSQLSynchQuery<Author> findByEmailAddressQuery = new OSQLSynchQuery<>(FIND_BY_EMAIL_ADDRESS);
    private OSQLSynchQuery<Author> findByNameQuery = new OSQLSynchQuery<>(FIND_BY_NAME);
    private OSQLSynchQuery<Author> findAllQuery = new OSQLSynchQuery<>(FIND_ALL);

    private static final String FIND_BY_EMAIL_ADDRESS = "SELECT FROM Author where emailAddress = ?";
    private static final String FIND_BY_NAME = "SELECT FROM Author where firstName = ? AND lastName = ?";
    private static final String FIND_ALL = "SELECT FROM Author";

    @Autowired
    public AuthorRepository(OrientStore orientStore) {
        this.orientStore = orientStore;
    }

    public Author newAuthor () {
        try  (ODatabaseObject db = orientStore.getSession()) {

            Author author = db.newInstance(Author.class);
            return db.detach(author);
        } catch (Exception ex) {
            logger.info("Cannot create author.", ex);
        }
        return null;
    }

    public Author newAuthor(String firstName, String lastName, String emailAddress) {
        try (ODatabaseObject db = orientStore.getSession()) {
            Author author = db.newInstance(Author.class);
            author.setFirstName(firstName);
            author.setLastName(lastName);
            author.setEmailAddress(emailAddress);
            return db.detach(author, true);
        } catch (Exception ex) {
            logger.info("Cannot create author.", ex);
        }
        return null;
    }

    ODatabaseObject getSession() {
        return orientStore.getSession();
    }

    public Author save(Author author) {

        try (ODatabaseObject db = orientStore.getSession()) {
            Author obj = db.save(author);
            return db.detach(obj, true);
        } catch (Exception ex) {
            logger.info("Unable to save author.", ex);
        }

        return null;

    }

    public Author findByEmailAddress(String emailAddress) {
        try (ODatabaseObject db = orientStore.getSession()) {
            List<Author> objs = db.command(findByEmailAddressQuery).execute(emailAddress);
            return db.detach(objs.get(0), true);
        } catch (Exception ex) {
            logger.info("Unable to save author.", ex);
        }

        return null;
    }

    public List<Author> findAll() {
        try (ODatabaseObject db = orientStore.getSession()) {
            return db.command(findAllQuery).execute();
        }
    }

    public boolean delete(Author author) {
        try (ODatabaseObject db = orientStore.getSession()) {
            db.delete(author);
        }
        return true;
    }

    public Author findByName(String firstName, String lastName) {
        try (ODatabaseObject db = orientStore.getSession()) {
            List<Author> objs = db.command(findByNameQuery).execute(firstName, lastName);
            return db.detach(objs.get(0), true);
        } catch (Exception ex) {
            logger.info("Unable to save author.", ex);
        }

        return null;
    }
}
