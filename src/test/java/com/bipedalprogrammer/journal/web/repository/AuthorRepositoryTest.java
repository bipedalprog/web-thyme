package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Author;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository repository;


    @Test
    public void saveShouldAssignId() {
        Author author = repository.newAuthor("Donald", "Duck", "donald@example.com");
        author.setFirstName("Donny");
        Author updated = repository.save(author);
        assertNotNull(updated);
        assertNotNull(updated.getAuthorId());
    }
}
