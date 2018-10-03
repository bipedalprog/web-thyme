package com.bipedalprogrammer.journal.web.controller;

import com.bipedalprogrammer.journal.web.model.Author;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/authors")
public class AuthorController {

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {

        return ResponseEntity.ok(author);
    }
}
