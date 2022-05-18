package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.ChatBaseTest;
import io.mkrzywanski.chat.app.chats.api.ChatCreatedResponse;
import io.mkrzywanski.chat.app.message.api.Message;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
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
        MessageDocument entity = new MessageDocument("user2", "hello", chatId, Instant.now());

        Flux<Message> messageFlux = requesterUser1.route("messages-stream")
                .retrieveFlux(Message.class);

        StepVerifier.create(messageFlux)
                .then(() -> messageRepository.save(entity))
                .expectNext(MessageMapper.fromMessageDocument(entity))
                .thenCancel();
    }
}
