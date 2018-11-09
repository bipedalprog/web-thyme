package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Author;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorRepository {
    private OrientStore orientStore;

    private Logger logger = LoggerFactory.getLogger(AuthorRepository.class);

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

    public Author save(Author author) {

        try (ODatabaseObject db = orientStore.getSession()) {
            Author obj = db.save(author);
            return db.detach(obj, true);
        } catch (Exception ex) {
            logger.info("Unable to save author.", ex);
        }

        return null;

    }

}
