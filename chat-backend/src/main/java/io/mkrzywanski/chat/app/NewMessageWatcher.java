package io.mkrzywanski.chat.app;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.mongodb.client.model.changestream.OperationType.INSERT;

@Component
class NewMessageWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewMessageWatcher.class);

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    NewMessageWatcher(final ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Flux<Message> newMessagesForChats(final Supplier<Mono<Set<UUID>>> chats, final String username) {
        Function<MessageDocument, Publisher<Boolean>> messageIsForThisUserChat =
                messageDocument -> chats.get().map(uuids -> !uuids.contains(messageDocument.getChatRoomId()));
        return reactiveMongoTemplate.changeStream(MessageDocument.class)
                .watchCollection("messages")
                .listen()
                .doOnNext(e -> LOGGER.info("event " + e))
                .filter(event -> event.getOperationType() == INSERT)
                .map(ChangeStreamEvent::getBody)
                .filter(m -> m.isNotFromUser(username))
                .filterWhen(messageIsForThisUserChat)
                .map(messageDocument -> new Message(messageDocument.getUsernameFrom(), messageDocument.getContent(), messageDocument.getChatRoomId()));
    }
}
