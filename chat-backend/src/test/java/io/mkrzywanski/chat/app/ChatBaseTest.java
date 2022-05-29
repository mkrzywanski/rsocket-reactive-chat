package io.mkrzywanski.chat.app;

import io.mkrzywanski.chat.ChatApplication;
import io.mkrzywanski.chat.app.chats.ChatToUserMappingsHolder;
import io.mkrzywanski.chat.app.chats.resuming.UserResumeTokenService;
import io.mkrzywanski.chat.app.message.MessageRepository;
import io.mkrzywanski.chat.keycloak.KeyCloakAccess;
import io.mkrzywanski.chat.keycloak.KeyCloakContainer;
import io.mkrzywanski.chat.keycloak.KeyCloakProperties;
import io.mkrzywanski.chat.keycloak.KeycloakInitializers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import java.net.URI;

import static io.mkrzywanski.chat.app.RSocketConstants.SIMPLE_AUTH;
import static io.mkrzywanski.chat.app.message.UserConstants.USER_1;
import static io.mkrzywanski.chat.app.message.UserConstants.USER_2;

@SpringBootTest(classes = {ChatApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = "spring.rsocket.server.port=0")
@DirtiesContext
public abstract class ChatBaseTest {

    private static final KeyCloakProperties KEY_CLOAK_PROPERTIES = KeycloakInitializers.keyCloakProperties();
    private static final KeyCloakContainer KEY_CLOAK_CONTAINER = new KeyCloakContainer(KEY_CLOAK_PROPERTIES.adminUser());
    private static final KeyCloakAccess keyCloakAccess;

    static {
        KEY_CLOAK_CONTAINER.start();
        KeycloakInitializers.setupKeycloak(KEY_CLOAK_PROPERTIES, KEY_CLOAK_CONTAINER.getFirstMappedPort());
        keyCloakAccess = KeycloakInitializers.keycloak(KEY_CLOAK_PROPERTIES, KEY_CLOAK_CONTAINER);
    }

    protected RSocketRequester requesterUser1;
    protected RSocketRequester requesterUser2;

    @Autowired
    protected RSocketRequester.Builder builder;

    @LocalRSocketServerPort
    protected int port;

    @Autowired
    protected ChatToUserMappingsHolder chatRoomUserMappings;

    @Autowired
    protected MessageRepository messageRepository;

    @Autowired
    protected UserResumeTokenService userResumeTokenService;

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        final var issuer = String.format("http://localhost:%s/auth/realms/%s", KEY_CLOAK_CONTAINER.getFirstMappedPort(), KEY_CLOAK_PROPERTIES.testRealm());
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> issuer);
    }


    @BeforeAll
    public void setupOnce() {
        requesterUser1 = setupUser1Requester();
        requesterUser2 = setupUser2Requester();
    }

    @AfterEach
    void tearDown() {
        final var clearUserChatMappings = chatRoomUserMappings.clear();
        final var deleteMessages = messageRepository.deleteAll();
        final var clearUser1Token = userResumeTokenService.deleteTokenForUser(USER_1);
        final var clearUser2Token = userResumeTokenService.deleteTokenForUser(USER_2);

        Mono.when(clearUserChatMappings, deleteMessages, clearUser1Token, clearUser2Token).block();
    }

    @AfterAll
    void afterAllTearDown() {
        requesterUser1.dispose();
        requesterUser2.dispose();
    }

    private RSocketRequester setupUser2Requester() {
        return setupRequesterFor(keyCloakAccess.getUser2Token());
    }

    private RSocketRequester setupUser1Requester() {
        return setupRequesterFor(keyCloakAccess.getUser1Token());
    }

    private RSocketRequester setupRequesterFor(final String token) {
        return builder
                .setupMetadata(new BearerTokenMetadata(token), SIMPLE_AUTH)
                .rsocketStrategies(v ->
                        v.encoder(new BearerTokenAuthenticationEncoder()))
                .websocket(URI.create("ws://localhost:" + port));
    }
}
