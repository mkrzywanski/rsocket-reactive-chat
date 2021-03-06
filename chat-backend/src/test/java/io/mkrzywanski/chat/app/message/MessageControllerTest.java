package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.ChatBaseTest;
import io.mkrzywanski.chat.app.chats.api.ChatCreatedResponse;
import io.mkrzywanski.chat.app.chats.api.JoinChatRequest;
import io.mkrzywanski.chat.app.message.api.InputMessage;
import io.mkrzywanski.chat.app.message.api.Message;
import io.mkrzywanski.chat.app.message.api.Page;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static io.mkrzywanski.chat.app.message.MongoTestConstants.BITNAMI_MONGODB_IMAGE;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.DATABASE;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.PASSWORD;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.USERNAME;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.WAIT_STRATEGY;
import static io.mkrzywanski.chat.app.message.UserConstants.USER_1;
import static io.mkrzywanski.chat.app.message.UserConstants.USER_2;
import static org.assertj.core.api.Assertions.assertThat;

class MessageControllerTest extends ChatBaseTest {

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
    void userCanCreateChat() {
        final var result = requesterUser1
                .route("create-chat")
                .data("create")
                .retrieveMono(ChatCreatedResponse.class);

        StepVerifier
                .create(result, 1)
                .consumeNextWith(message -> {
                    assertThat(message.chatId()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void userCanJoinExistingChat() {
        final var chatId = requesterUser1
                .route("create-chat")
                .data("create")
                .retrieveMono(ChatCreatedResponse.class)
                .map(ChatCreatedResponse::chatId);

        final var result = requesterUser2.route("join-chat")
                .data(new JoinChatRequest(chatId.block()))
                .retrieveMono(Boolean.class);

        StepVerifier
                .create(result)
                .consumeNextWith(message -> assertThat(message).isTrue())
                .verifyComplete();
    }


    @Test
    void user1ShouldGetMessagesFromUser2() throws InterruptedException {
        //user1 creates chat
        final UUID chatId = requesterUser1
                .route("create-chat")
                .data("create")
                .retrieveMono(ChatCreatedResponse.class)
                .map(ChatCreatedResponse::chatId)
                .block();

        //user2 joins chat
        final Boolean joiningResult = requesterUser2.route("join-chat")
                .data(new JoinChatRequest(chatId))
                .retrieveMono(Boolean.class)
                .block();

        assert Boolean.TRUE.equals(joiningResult);

        //user1 wants to send this message
        final var messageFromUser1 = Flux.just(new InputMessage("user1", "hello from user1 test1", chatId));

        //sends user 1 messages and awaits for messages from chats that this user is part of
        final var incomingMessagesForUser1 = requesterUser1
                .route("chat-channel")
                .data(messageFromUser1)
                .retrieveFlux(Message.class);

        //user2 message
        final var messagesFromUser2 = Flux.just(new InputMessage("user2", "hello from user2 test1", chatId));

        //sends user 1 messages and awaits for messages from chats that this user is part of
        final var incomingMessagesForUser2 = requesterUser2
                .route("chat-channel")
                .data(messagesFromUser2)
                .retrieveFlux(Message.class);

        //subscribe to user1 flux and delay it by 1 second so that user2 channel can send messages and user1 receive them
        final var subscription = incomingMessagesForUser1
                .subscribeOn(Schedulers.boundedElastic())
                .delaySubscription(Duration.ofSeconds(1))
                .subscribe();

        StepVerifier
                .create(incomingMessagesForUser2, 1)
                .consumeNextWith(message -> {
                    assertThat(message.usernameFrom()).isEqualTo(USER_1);
                    assertThat(message.content()).isEqualTo("hello from user1 test1");
                    assertThat(message.chatRoomId()).isEqualTo(chatId);
                })
                .thenCancel()
                .verify();


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

    @Test
    void shouldGetUserChats() {
        //given
        final UUID chatId = requesterUser1
                .route("create-chat")
                .data("aaa")
                .retrieveMono(ChatCreatedResponse.class)
                .map(ChatCreatedResponse::chatId)
                .block();

        //when
        final Mono<Set<UUID>> chatIds = requesterUser1
                .route("get-user-chats")
                .retrieveMono(new ParameterizedTypeReference<>() {
                });

        //then
        StepVerifier.create(chatIds)
                .expectNext(Set.of(chatId))
                .verifyComplete();
    }

    @Test
    void shouldGetAllChatMessagesForRequestingUser() {

        //given
        final UUID chatId = UUID.fromString("41bd1c40-d320-475b-bd61-16146e275ee4");
        final Instant now = Clock.systemUTC().instant();
        final MessageDocument m1 = new MessageDocument(USER_1, "hello user 2", chatId, now.plusSeconds(1));
        messageRepository.save(m1).block();
        final MessageDocument m2 = new MessageDocument(USER_2, "hello user 1", chatId, now.plusSeconds(2));
        messageRepository.save(m2).block();

        chatRoomUserMappings.putUserToChat(Mono.just(USER_1), chatId).block();
        chatRoomUserMappings.putUserToChat(Mono.just(USER_2), chatId).block();

        //when
        final var messageFlux = requesterUser1
                .route("chat." + chatId + ".messages")
                .retrieveFlux(Message.class);

        //then
        StepVerifier.create(messageFlux)
                .expectNext(MessageMapper.fromMessageDocument(m2))
                .expectNext(MessageMapper.fromMessageDocument(m1))
                .verifyComplete();

    }

    @Test
    void shouldGetPagedResults() {
        //given
        final UUID chatId = UUID.fromString("41bd1c40-d320-475b-bd61-16146e275ee4");
        final Instant now = Clock.systemUTC().instant();
        final MessageDocument m1 = new MessageDocument(USER_1, "hello user 2", chatId, now.plusSeconds(1));
        final MessageDocument m2 = new MessageDocument(USER_2, "hello user 1", chatId, now.plusSeconds(2));
        final MessageDocument m3 = new MessageDocument(USER_2, "test message", chatId, now.plusSeconds(3));
        final MessageDocument m4 = new MessageDocument(USER_2, "test message 2", chatId, now.plusSeconds(4));

        messageRepository.save(m1).block();
        messageRepository.save(m2).block();
        messageRepository.save(m3).block();
        messageRepository.save(m4).block();

        chatRoomUserMappings.putUserToChat(Mono.just(USER_1), chatId).block();
        chatRoomUserMappings.putUserToChat(Mono.just(USER_2), chatId).block();

        //when
        final var messageFlux = requesterUser1
                .route("chat." + chatId + ".messages.paged")
                .data(new Page(0, 1))
                .retrieveFlux(Message.class);

        final var messageFlux2 = requesterUser1
                .route("chat." + chatId + ".messages.paged")
                .data(new Page(0, 2))
                .retrieveFlux(Message.class);

        //then
        StepVerifier.create(messageFlux)
                .expectNext(MessageMapper.fromMessageDocument(m4))
                .verifyComplete();

        StepVerifier.create(messageFlux2)
                .expectNext(MessageMapper.fromMessageDocument(m4))
                .expectNext(MessageMapper.fromMessageDocument(m3))
                .verifyComplete();
    }
}
