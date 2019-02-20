package com.bipedalprogrammer.journal.web.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class User implements UserDetails {
    public static final String DEFAULT_USER = "default@bipedalprogrammer.com";
    public static final String ROLE_USER = "ROLE_USER";

    private String password;
    private String email;
    private boolean enabled;
    private Set<GrantedAuthority> authorities;

    public User() {
        email = DEFAULT_USER;
        authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        enabled = false;
    }

    public User(String username, String password, boolean enabled) {
        this();
        this.email = username;
        this.password = password;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String email) {
        this.email = email;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String>  getRoles() {
        return authorities.stream().map(a -> a.getAuthority().toString()).collect(Collectors.toSet());
    }

    public boolean grant(String roleName) {
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        if (!authorities.contains(authority)) {
            authorities.add(authority);
            return true;
        }
        return false;
    }

    public boolean revoke(String roleName) {
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        if (authorities.contains(authority)) {
            authorities.remove(authority);
            return true;
        }
        return false;
    }

    public boolean isAuthorized(String roleName) {
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        return authorities.contains(authority);
    }
}
