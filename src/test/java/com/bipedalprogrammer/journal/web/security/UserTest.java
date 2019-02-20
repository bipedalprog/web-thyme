package com.bipedalprogrammer.journal.web.security;

import org.junit.Test;


import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {
    @Test
    public void newUserHasUserRole() {
        User user = new User();
        assertThat(user.getRoles(), hasItem(User.ROLE_USER));
    }

    @Test
    public void roleGrantedWorks() {
        final String TEST_ROLE = "TEST_ROLE";
        User user = new User();
        user.grant(TEST_ROLE);
        assertTrue(user.isAuthorized(TEST_ROLE));
    }

    @Test
    public void roleRevokeWorks() {
        User user = new User();
        user.revoke(User.ROLE_USER);
        assertFalse(user.isAuthorized(User.ROLE_USER));
    }

    @Test
    public void multipleRolesAreSupported() {
        User user = new User();
        final String TEST_ROLE = "TEST_ROLE";
        user.grant(TEST_ROLE);
        Set<String> roles = user.getRoles();
        assertThat(roles, hasItem(User.ROLE_USER));
        assertThat(roles, hasItem(TEST_ROLE));
    }
}
