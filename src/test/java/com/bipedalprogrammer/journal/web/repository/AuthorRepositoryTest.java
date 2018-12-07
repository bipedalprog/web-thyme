package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Author;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository repository;


    @Before
    public void prepareRepository() {
        List<Author> priors = repository.findAll();
        priors.forEach( (a) -> repository.delete(a));
    }

    @Test
    public void saveShouldAssignId() {
        Author author = repository.newAuthor("Donald", "Duck", "donald@example.com");
        author.setFirstName("Donny");
        Author updated = repository.update(author);
        assertNotNull(updated);
        assertThat(updated.getFirstName(), equalTo(author.getFirstName()));
    }

    @Test
    public void updateShouldChangeEntity() {
        Author author = repository.newAuthor("William", "Shatner", "kirk@example.com");
        author.setEmailAddress("tjhooker@example.com");
        Author updated = repository.update(author);
        assertEquals(author.getAuthorId(), updated.getAuthorId());
    }
    @Test
    public void loadAuthorByEmail() {
        Author created = repository.newAuthor("Isaac", "Asimov", "asimov@example.com");
        assertNotNull(created);
        Author found = repository.findByEmailAddress(created.getEmailAddress());
        assertNotNull(found);
        assertThat(found.getAuthorId(), equalTo(created.getAuthorId()));
    }

//    @Test
//    public void loadAuthorByName() {
//        Author created = repository.newAuthor("Isaac", "Asimov", "asimmov@example.com");
//        Author stored = repository.update(created);
//        assertNotNull(stored);
//        Author found = repository.findByName(stored.getFirstName(), stored.getLastName());
//        assertNotNull(found);
//        assertThat(found.getAuthorId(), equalTo(stored.getAuthorId()));
//    }
}
