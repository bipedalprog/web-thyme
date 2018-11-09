package com.bipedalprogrammer.journal.web.config;

public interface DatabaseConfiguration {
    String getConnectionString();

    void setConnectionString(String connectionString);

    String getUser();

    void setUser(String user);

    String getPassword();

    void setPassword(String password);
}
