package com.example.User.security;

import java.util.List;

public class AuthenticatedUserPrincipal {
    private final String username;
    private final String userId;
    private final List<String> roles;

    public AuthenticatedUserPrincipal(String username, String userId, List<String> roles) {
        this.username = username;
        this.userId = userId;
        this.roles = roles;
    }

    public String getUsername() { return username; }
    public String getUserId() { return userId; }
    public List<String> getRoles() { return roles; }
}
