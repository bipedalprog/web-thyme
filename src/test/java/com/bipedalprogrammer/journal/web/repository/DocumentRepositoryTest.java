package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Author;
import com.bipedalprogrammer.journal.web.model.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.bipedalprogrammer.journal.web.model.Author.AUTHOR_DEFAULT_ID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentRepositoryTest {
    @Autowired
    private Persistor repository;

    @Test
    public void saveShouldAssignId() {
        Set<Author> authors = new HashSet<>();
        authors.add(new Author("Sample", "Author", "sample@example.com"));
        Document document = new Document();
        document.setTitle("A Test Document");
        document.setAuthors(authors);
        document.setRevision("1.0");
        document.setRevisionDate(new Date());
        document.setBody("We're all bozos on this bus.");
        Document updated = repository.newDocument(document);
        assertNotNull(updated);
        assertNotNull(document.getAuthors());
        assertThat(document.getAuthors(), hasSize(1));
        assertThat(document.getAuthors().iterator().next().getAuthorId(), not(equalTo(AUTHOR_DEFAULT_ID)));
    }

    @Test
    public void saveShouldRecognizeNewAuthors() {
        // Set up.
        Set<Author> initialAuthors = new HashSet<>();
        initialAuthors.add(new Author("Initial", "Author", "sample@example.com"));
        Document document = new Document();
        document.setTitle("Additional Authors Test");
        document.setAuthors(initialAuthors);
        document.setRevision("1.0");
        document.setRevisionDate(new Date());
        document.setBody("This is the initial list of authors.");
        Document updated = repository.newDocument(document);
        assertNotNull(updated);

        // Mutate.
        updated.getAuthors().add(new Author("Updated", "Author", "updated@example.com"));
        Document mutated = repository.save(updated);
        assertNotNull(mutated);
    }
}
