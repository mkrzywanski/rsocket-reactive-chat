package io.mkrzywanski.chat.app;

import io.mkrzywanski.chat.ChatApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static io.mkrzywanski.chat.app.MongoTestConstants.BITNAMI_MONGODB_IMAGE;
import static io.mkrzywanski.chat.app.MongoTestConstants.DATABASE;
import static io.mkrzywanski.chat.app.MongoTestConstants.PASSWORD;
import static io.mkrzywanski.chat.app.MongoTestConstants.USERNAME;
import static io.mkrzywanski.chat.app.MongoTestConstants.WAIT_STRATEGY;
import static io.mkrzywanski.chat.app.RSocketConstants.SIMPLE_AUTH;
import static io.mkrzywanski.chat.app.UserConstants.USER_1;
import static io.mkrzywanski.chat.app.UserConstants.USER_2;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ChatApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = "spring.rsocket.server.port=0")
@DirtiesContext
class StreamResumingTest {

    private static final GenericContainer<?> MONGO_DB_CONTAINER = new GenericContainer<>(BITNAMI_MONGODB_IMAGE)
            .withEnv("MONGODB_USERNAME", USERNAME)
            .withEnv("MONGODB_PASSWORD", PASSWORD)
            .withEnv("MONGODB_DATABASE", DATABASE)
            .withEnv("MONGODB_REPLICA_SET_MODE", "primary")
            .withEnv("MONGODB_REPLICA_SET_KEY", "someKey1")
            .withEnv("MONGODB_ROOT_PASSWORD", "password")
            .waitingFor(WAIT_STRATEGY)
            .withExposedPorts(27017);

    private RSocketRequester requesterUser1;
    private RSocketRequester requesterUser2;

    @Autowired
    private RSocketRequester.Builder builder;

    @LocalRSocketServerPort
    private int port;

    @Autowired
    private MongoChatToUserMappingsHolder chatRoomUserMappings;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserResumeTokenService userResumeTokenService;

    static {
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.username", () -> "root");
        registry.add("spring.data.mongodb.password", () -> "password");
        registry.add("spring.data.mongodb.replicaSet", () -> "replicaset");
        registry.add("spring.data.mongodb.database", () -> DATABASE);
        registry.add("spring.data.mongodb.port", MONGO_DB_CONTAINER::getFirstMappedPort);
        registry.add("spring.data.mongodb.authentication-database", () -> "admin");
    }

    @BeforeAll
    public void setupOnce() {
        requesterUser1 = setupUser1Requester();
        requesterUser2 = setupUser2Requester();
    }

    @AfterAll
    void afterAll() {
        MONGO_DB_CONTAINER.stop();
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
                .tcp("localhost", port);
    }

    @AfterEach
    void tearDown() {
        final var clearUserChatMappings = chatRoomUserMappings.clear();
        final var deleteMessages = messageRepository.deleteAll();
        final var clearUser1Token = userResumeTokenService.deleteTokenForUser(USER_1);
        final var clearUser2Token = userResumeTokenService.deleteTokenForUser(USER_2);

        Mono.when(clearUserChatMappings, deleteMessages, clearUser1Token, clearUser2Token).subscribe();
    }

    @Test
    void userShouldReceiveMessagesAfterReconnect() throws InterruptedException {
        //user1 creates chat
        final UUID chat = requesterUser1
                .route("create-chat")
                .retrieveMono(ChatCreatedResponse.class)
                .map(ChatCreatedResponse::chatId)
                .block();

        //user2 joins chat
        final Boolean joiningResult = requesterUser2.route("join-chat")
                .data(new JoinChatRequest(chat))
                .retrieveMono(Boolean.class)
                .block();

        assert Boolean.TRUE.equals(joiningResult);

        //user1 sink so that we can push to flux on demand
        final var user1Sink = Sinks.many().unicast().onBackpressureBuffer();
        final var messageFromUser1 = user1Sink.asFlux();

        //sends user1 messages and awaits for messages from chats that this user is part of
        final var incomingMessagesForUser1 = requesterUser1
                .route("chat-channel")
                .data(messageFromUser1)
                .retrieveFlux(Message.class);

        //sends user2 messages and awaits for messages from chats that this user is part of
        final var incomingMessagesForUser2 = requesterUser2
                .route("chat-channel")
                .data(Flux.empty())
                .retrieveFlux(Message.class);

        //subscribe to user1 flux and delay it by 1 second so that user2 channel can send messages and user1 receive them
        final var subscription = incomingMessagesForUser1
                .subscribeOn(Schedulers.boundedElastic())
                .delaySubscription(Duration.ofSeconds(1))
                .subscribe();

        //user2 receives first message and cancel subscription to close connection
        StepVerifier
                .create(incomingMessagesForUser2)
                .then(() -> user1Sink.emitNext(new Message(USER_1, "hello from user1", chat), Sinks.EmitFailureHandler.FAIL_FAST))
                .consumeNextWith(message -> {
                    assertThat(message.usernameFrom()).isEqualTo(USER_1);
                    assertThat(message.content()).isEqualTo("hello from user1");
                    assertThat(message.chatRoomId()).isEqualTo(chat);
                })
                .thenCancel()
                .verify();

        //user1 still sends messages to chat
        user1Sink.emitNext(new Message(USER_1, "hello from user1 again", chat), Sinks.EmitFailureHandler.FAIL_FAST);

        //user2 reconnects
        final var incomingMessagesForUser2AfterReconnect = requesterUser2
                .route("chat-channel")
                .data(Flux.empty())
                .retrieveFlux(Message.class);

        //user2 should receive messages after reconnect
        StepVerifier
                .create(incomingMessagesForUser2AfterReconnect)
                .consumeNextWith(message -> {
                    assertThat(message.usernameFrom()).isEqualTo(USER_1);
                    assertThat(message.content()).isEqualTo("hello from user1 again");
                    assertThat(message.chatRoomId()).isEqualTo(chat);
                });

        final var user1Token = userResumeTokenService.getResumeTimestampFor(USER_1);
        StepVerifier.create(user1Token)
                .expectNextCount(1)
                .verifyComplete();

        final var user2Token = userResumeTokenService.getResumeTimestampFor(USER_2);
        StepVerifier.create(user2Token)
                .expectNextCount(1)
                .verifyComplete();

        subscription.dispose();
    }
}
