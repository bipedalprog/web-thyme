package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.security.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserPersistorTest {
    public static final String TEST_USER = "testuser@example.com";
    public static final String TEST_PASSWORD = "chooseWisely";

    @Autowired
    private UserPersistence persistence;

    @After
    public void prepareTestTable() {
        List<User> priors = persistence.findAll();
        priors.forEach(u -> persistence.delete(u));
    }

    @Test
    public void addUserShouldCreateNewUser() {
        assertTrue(persistence.addUser(TEST_USER, TEST_PASSWORD));
    }

    @Test
    public void addUserShouldFailForSameUser() {
        assertTrue(persistence.addUser(TEST_USER, TEST_PASSWORD));
        assertFalse(persistence.addUser(TEST_USER, TEST_PASSWORD));
    }

}
