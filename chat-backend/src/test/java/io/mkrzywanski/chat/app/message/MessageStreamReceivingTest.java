package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.ChatBaseTest;
import io.mkrzywanski.chat.app.chats.api.ChatCreatedResponse;
import io.mkrzywanski.chat.app.message.api.Message;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Instant;

class MessageStreamReceivingTest extends ChatBaseTest {

    @Test
    void shouldReceiveStreamOfMessages() {
        final var chatId = requesterUser1
                .route("create-chat")
                .data("create")
                .retrieveMono(ChatCreatedResponse.class)
                .map(ChatCreatedResponse::chatId)
                .block();
        final var messageDocument = new MessageDocument("user2", "hello", chatId, Instant.now());

        final var messageStream = requesterUser1.route("messages-stream")
                .retrieveFlux(Message.class);

        StepVerifier.create(messageStream)
                .then(() -> messageRepository.save(messageDocument))
                .expectNext(MessageMapper.fromMessageDocument(messageDocument))
                .thenCancel();
    }
}
