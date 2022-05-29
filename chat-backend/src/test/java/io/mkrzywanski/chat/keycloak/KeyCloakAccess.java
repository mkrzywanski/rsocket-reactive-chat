package io.mkrzywanski.chat.keycloak;

import org.keycloak.admin.client.Keycloak;

public record KeyCloakAccess(Keycloak keycloakAdminAccess, Keycloak user1Access, Keycloak user2access) {
    public String getUser1Token() {
        return user1Access.tokenManager().getAccessToken().getToken();
    }

    public String getUser2Token() {
        return user2access.tokenManager().getAccessToken().getToken();
    }
}
