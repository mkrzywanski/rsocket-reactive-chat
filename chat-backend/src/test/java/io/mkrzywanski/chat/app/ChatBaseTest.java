package io.mkrzywanski.chat.app;

import io.mkrzywanski.chat.ChatApplication;
import io.mkrzywanski.chat.app.chats.ChatToUserMappingsHolder;
import io.mkrzywanski.chat.app.chats.resuming.UserResumeTokenService;
import io.mkrzywanski.chat.app.message.MessageRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import java.net.URI;

import static io.mkrzywanski.chat.app.RSocketConstants.SIMPLE_AUTH;
import static io.mkrzywanski.chat.app.UserConstants.USER_1;
import static io.mkrzywanski.chat.app.UserConstants.USER_2;

@SpringBootTest(classes = {ChatApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = "spring.rsocket.server.port=0")
@DirtiesContext
public abstract class ChatBaseTest {

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

        Mono.when(clearUserChatMappings, deleteMessages, clearUser1Token, clearUser2Token).subscribe();
    }

    @AfterAll
    void afterAllTearDown() {
        requesterUser1.dispose();
        requesterUser2.dispose();
    }

    private RSocketRequester setupUser2Requester() {
        final var user2 = new UsernamePasswordMetadata(USER_2, "pass");
        return setupRequesterFor(user2);
    }

    private RSocketRequester setupUser1Requester() {
        final var user1 = new UsernamePasswordMetadata(USER_1, "pass");
        return setupRequesterFor(user1);
    }

    private RSocketRequester setupRequesterFor(final UsernamePasswordMetadata usernamePasswordMetadata) {
        return builder
                .setupMetadata(usernamePasswordMetadata, SIMPLE_AUTH)
                .rsocketStrategies(v ->
                        v.encoder(new SimpleAuthenticationEncoder()))
                .websocket(URI.create("ws://localhost:" + port));
    }
}
