package io.mkrzywanski.chat.app.common;

import org.springframework.security.oauth2.jwt.Jwt;

public final class Jwtutil {

    private Jwtutil() {
    }

    public static String extractUserName(final Jwt jwt) {
        return jwt.getClaimAsString("preferred_username");
    }
}
