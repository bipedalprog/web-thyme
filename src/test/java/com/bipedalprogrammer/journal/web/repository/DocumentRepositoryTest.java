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
    private DocumentRepository repository;

    @Test
    public void saveShouldAssignId() {
        List<Author> authorList = new ArrayList<>();
        authorList.add(new Author("Sample", "Author", "sample@example.com"));
        Set<String> tags = new HashSet<>();
        tags.add("test");
        Document document = repository.newDocument();
        document.setAuthors(authorList);
        document.setRevision("1.0");
        document.setRevisionDate(new Date());
        document.setBody("We're all bozos on this bus.");
        document.setTags(tags);
        Document updated = repository.save(document);
        assertNotNull(updated);
        assertNotNull(document.getDocumentId());
    }
}
