package com.bipedalprogrammer.journal.web.repository;

import com.bipedalprogrammer.journal.web.security.User;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.bipedalprogrammer.journal.web.repository.OrientStore.*;

@Component
public class UserPersistence {

    private OrientStore orientStore;

    private Logger logger = LoggerFactory.getLogger(UserPersistence.class);

    private static final String FIND_USERS_BY_NAME = "SELECT FROM Users WHERE username = ?";

    @Autowired
    public UserPersistence(OrientStore orientStore) {
        this.orientStore = orientStore;
    }

    public User findByUsername(String username) {
        User user = new User();
        try (ODatabaseSession db = orientStore.getSession()) {
            try (OResultSet rs = db.query(FIND_USERS_BY_NAME, username)) {
                if (rs.hasNext()) {
                    OResult result = rs.next();
                    result.getVertex().ifPresent(v -> {
                        userFromVertex(user, v);
                    });
                }
            }
        } catch (Exception ex) {
            logger.info("Unable to load user.", ex);
        }
        return user;
    }

    private void userFromVertex(User u, OVertex v) {
        u.setUsername(v.getProperty(USER_EMAIL));
        u.setPassword(v.getProperty(USER_PASSWORD));
        u.setEnabled(v.getProperty(USER_ENABLED));
        String roles = v.getProperty(USER_ROLES);
        String[] roleNames = roles.split(",");
        for (String name : roleNames) {
            u.grant(name);
        }
    }

}
