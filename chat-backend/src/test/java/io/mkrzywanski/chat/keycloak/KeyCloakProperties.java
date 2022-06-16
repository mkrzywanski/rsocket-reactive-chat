package io.mkrzywanski.chat.keycloak;

public record KeyCloakProperties(KeycloakClient client,
        KeycloakClient adminCliClient,
        KeycloakUser user1,
        KeycloakUser user2,
        KeycloakUser adminUser,
        String testRealm) {

    public static final KeycloakClient ADMIN_CLI_CLIENT = new KeycloakClient("admin-cli", null);
}
