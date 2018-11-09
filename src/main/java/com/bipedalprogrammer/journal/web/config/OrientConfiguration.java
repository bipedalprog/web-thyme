package com.bipedalprogrammer.journal.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("orientdb")
public class OrientConfiguration implements DatabaseConfiguration {
    private String connectionString;
    private String user;
    private String password;

    @Override
    public String getConnectionString() {
        return connectionString;
    }

    @Override
    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}
