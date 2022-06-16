package io.mkrzywanski.chat.keycloak;

public record KeycloakUser(String username,
                           String password,
                           String email) {
}
