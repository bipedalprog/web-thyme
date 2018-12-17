package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.model.Author;
import com.bipedalprogrammer.journal.web.model.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.assertNotNull;

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
        assertNotNull(document.getDocumentId());
    }
}
