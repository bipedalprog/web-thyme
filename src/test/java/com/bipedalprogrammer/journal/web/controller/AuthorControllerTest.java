package com.bipedalprogrammer.journal.web.controller;

import com.bipedalprogrammer.journal.web.model.Author;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("test")
public class AuthorControllerTest {

    @Autowired
    private AuthorController controller;


    @Test
    public void postOfAuthorShouldReturnUpdatedAuthor() {
        final Author author = new Author("Donald", "Duck", "dduck@example.com");
        final ResponseEntity<Author> result = controller.createAuthor(author);
        assertThat(result.getStatusCodeValue(), equalTo(200));
        assertThat(result.getBody(), equalTo(author));
    }
}
