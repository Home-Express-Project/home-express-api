package com.homeexpress.home_express_api.config;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;

/**
 * Lightweight authenticated user representation backed directly by JWT claims.
 * Avoids per-request database lookups while still exposing the essentials for
 * authorization and auditing.
 */
public record JwtAuthenticatedUser(Long userId, String email, String role) implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String getName() {
        if (email != null) {
            return email;
        }
        return userId != null ? userId.toString() : "";
    }

    // Bean-style getters for frameworks that expect them
    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
