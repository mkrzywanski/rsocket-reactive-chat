package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.ChatBaseTest;
import io.mkrzywanski.chat.app.chats.api.ChatCreatedResponse;
import io.mkrzywanski.chat.app.message.api.InputMessage;
import io.mkrzywanski.chat.app.message.api.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import reactor.test.StepVerifier;

import static io.mkrzywanski.chat.app.message.MongoTestConstants.BITNAMI_MONGODB_IMAGE;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.DATABASE;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.PASSWORD;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.USERNAME;
import static io.mkrzywanski.chat.app.message.MongoTestConstants.WAIT_STRATEGY;
import static org.assertj.core.api.Assertions.assertThat;

class SendingMessageTest extends ChatBaseTest {

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
    void shouldSendMessageWithFireAndForget() {
        final var chatId = requesterUser1
                .route("create-chat")
                .data("create")
                .retrieveMono(ChatCreatedResponse.class)
                .map(ChatCreatedResponse::chatId)
                .block();

        final var data = new InputMessage("user1", "hello from user1 test1", chatId);
        final var mono = requesterUser1
                .route("send-message")
                .data(data)
                .retrieveMono(Message.class);

        StepVerifier.create(mono)
                .consumeNextWith(message -> {
                    assertThat(message.chatRoomId()).isEqualTo(chatId);
                    assertThat(message.content()).isEqualTo(data.content());
                    assertThat(message.time()).isNotNull();
                    assertThat(message.usernameFrom()).isEqualTo(data.usernameFrom());
                })
                .verifyComplete();

        StepVerifier.create(messageRepository.findAll())
                .consumeNextWith(messageDocument -> {
                    assertThat(messageDocument.getUsernameFrom()).isEqualTo("user1");
                })
                .verifyComplete();

    }
}
