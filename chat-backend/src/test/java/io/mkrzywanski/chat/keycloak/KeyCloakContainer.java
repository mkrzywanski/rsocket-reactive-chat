package io.mkrzywanski.chat.keycloak;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class KeyCloakContainer extends GenericContainer<KeyCloakContainer> {

    private static final DockerImageName KEYCLOAK_IMAGE = DockerImageName.parse("jboss/keycloak:16.1.0");
    private static final int PORT = 8080;

    private final KeycloakUser admin;

    public KeyCloakContainer(final KeycloakUser admin) {
        super(KEYCLOAK_IMAGE);
        this.admin = admin;
    }

    @Override
    protected void configure() {
        withEnv("KEYCLOAK_HTTP_PORT", String.valueOf(PORT));
        withEnv("KEYCLOAK_USER", admin.username());
        withEnv("KEYCLOAK_PASSWORD", admin.password());
        withExposedPorts(PORT);
        waitingFor(Wait.forListeningPort());
    }

    @Override
    public void close() {
        super.close();
    }
}
