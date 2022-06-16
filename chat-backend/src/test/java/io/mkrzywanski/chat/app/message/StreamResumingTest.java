package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.ChatBaseTest;
import io.mkrzywanski.chat.app.chats.api.ChatCreatedResponse;
import io.mkrzywanski.chat.app.chats.api.JoinChatRequest;
import io.mkrzywanski.chat.app.message.api.InputMessage;
import io.mkrzywanski.chat.app.message.api.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static io.mkrzywanski.chat.app.message.MongoTestConstants.BITNAMI_MONGODB_IMAGE;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.DATABASE;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.PASSWORD;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.USERNAME;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.WAIT_STRATEGY;
import static io.mkrzywanski.chat.app.message.UserConstants.USER_1;
import static io.mkrzywanski.chat.app.message.UserConstants.USER_2;
import static org.assertj.core.api.Assertions.assertThat;

class StreamResumingTest extends ChatBaseTest {

    private static final GenericContainer<?> MONGO_DB_CONTAINER = new GenericContainer<>(BITNAMI_MONGODB_IMAGE)
            .withEnv("MONGODB_USERNAME", USERNAME)
            .withEnv("MONGODB_PASSWORD", PASSWORD)
            .withEnv("MONGODB_DATABASE", DATABASE)
            .withEnv("MONGODB_REPLICA_SET_MODE", "primary")
            .withEnv("MONGODB_REPLICA_SET_KEY", "someKey1")
            .withEnv("MONGODB_ROOT_PASSWORD", "password")
            .waitingFor(WAIT_STRATEGY)
            .withExposedPorts(27017);

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

    @AfterAll
    void afterAll() {
        MONGO_DB_CONTAINER.stop();
    }

    @Test
    void userShouldReceiveMessagesAfterReconnect() throws InterruptedException {
        //user1 creates chat
        final UUID chat = requesterUser1
                .route("create-chat")
                .data("create")
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
                .then(() -> user1Sink.emitNext(new InputMessage(USER_1, "hello from user1", chat), Sinks.EmitFailureHandler.FAIL_FAST))
                .consumeNextWith(message -> {
                    assertThat(message.usernameFrom()).isEqualTo(USER_1);
                    assertThat(message.content()).isEqualTo("hello from user1");
                    assertThat(message.chatRoomId()).isEqualTo(chat);
                })
                .thenCancel()
                .verify();

        //user1 still sends messages to chat
        user1Sink.emitNext(new InputMessage(USER_1, "hello from user1 again", chat), Sinks.EmitFailureHandler.FAIL_FAST);

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

        final var user1Token = userResumeTokenService.getResumeTimestampFor(Mono.just(USER_1));
        StepVerifier.create(user1Token)
                .expectNextCount(1)
                .verifyComplete();

        final var user2Token = userResumeTokenService.getResumeTimestampFor(Mono.just(USER_2));
        StepVerifier.create(user2Token)
                .expectNextCount(1)
                .verifyComplete();

        subscription.dispose();
    }
}
