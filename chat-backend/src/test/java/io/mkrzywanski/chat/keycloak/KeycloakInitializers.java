package io.mkrzywanski.chat.keycloak;

import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Arrays;
import java.util.List;

public class KeycloakInitializers {

    public static KeyCloakContainer keyCloakContainer(final KeyCloakProperties keyCloakProperties) {
        final var keyCloakContainer = new KeyCloakContainer(keyCloakProperties.adminUser());
        keyCloakContainer.start();
        setupKeycloak(keyCloakProperties, keyCloakContainer.getFirstMappedPort());
        return keyCloakContainer;
    }

    public static KeyCloakProperties keyCloakProperties() {
        final var client = new KeycloakClient("test-client", "secret");
        final var user1 = new KeycloakUser("user1", "test", "user1@gmail.com");
        final var user2 = new KeycloakUser("user2", "test", "user2@gmail.com");
        final var admin = new KeycloakUser("admin", "admin", "admin@admin.com");
        return new KeyCloakProperties(client, KeyCloakProperties.ADMIN_CLI_CLIENT, user1, user2, admin, "xD");
    }

    public static KeyCloakAccess keycloak(final KeyCloakProperties keyCloakProperties, final KeyCloakContainer keyCloakContainer) {
        final String serverUrl = String.format("http://localhost:%s/auth", keyCloakContainer.getFirstMappedPort());
        final var adminAccess = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId(keyCloakProperties.adminCliClient().clientId())
                .username(keyCloakProperties.adminUser().username())
                .password(keyCloakProperties.adminUser().password())
                .build();
        final var user1access = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(keyCloakProperties.testRealm())
                .clientId(keyCloakProperties.client().clientId())
                .clientSecret(keyCloakProperties.client().clientSecret())
                .username(keyCloakProperties.user1().username())
                .password(keyCloakProperties.user1().password())
                .build();
        final var user2Access = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(keyCloakProperties.testRealm())
                .clientId(keyCloakProperties.client().clientId())
                .clientSecret(keyCloakProperties.client().clientSecret())
                .username(keyCloakProperties.user2().username())
                .password(keyCloakProperties.user2().password())
                .build();
        return new KeyCloakAccess(adminAccess, user1access, user2Access);
    }

    public static void setupKeycloak(final KeyCloakProperties keyCloakProperties,
                                     final int port) {
        final var keycloak = KeycloakBuilder.builder()
                .serverUrl(String.format("http://localhost:%s/auth", port))
                .realm("master")
                .clientId(keyCloakProperties.adminCliClient().clientId())
                .username(keyCloakProperties.adminUser().username())
                .password(keyCloakProperties.adminUser().password())
                .build();

        final var realm = testRealm(keyCloakProperties.testRealm());
        keycloak.realms().create(realm);

        final var clientRepresentation = testClient(keyCloakProperties.client());
        keycloak.realm(keyCloakProperties.testRealm()).clients().create(clientRepresentation);

        final var user1 = testUser(keyCloakProperties.user1());
        keycloak.realm(keyCloakProperties.testRealm()).users().create(user1);

        final var user2 = testUser(keyCloakProperties.user2());
        keycloak.realm(keyCloakProperties.testRealm()).users().create(user2);

    }

    private static UserRepresentation testUser(final KeycloakUser keycloakUser) {
        final var credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(keycloakUser.password());

        final var user = new UserRepresentation();
        user.setUsername(keycloakUser.username());
        user.setFirstName("test");
        user.setLastName("test");
        user.setEmail(keycloakUser.email());
        user.setCredentials(List.of(credential));
        user.setEnabled(true);
        user.setRealmRoles(List.of("admin"));
        return user;
    }

    private static RealmRepresentation testRealm(final String realm) {
        final var rep = new RealmRepresentation();
        rep.setRealm(realm);
        rep.setEnabled(true);
        return rep;
    }

    private static ClientRepresentation testClient(final KeycloakClient keycloakClient) {
        final var clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(keycloakClient.clientId());
        clientRepresentation.setSecret(keycloakClient.clientSecret());
        clientRepresentation.setRedirectUris(Arrays.asList("*"));
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setStandardFlowEnabled(true);
        clientRepresentation.setServiceAccountsEnabled(true);
        return clientRepresentation;
    }
}
