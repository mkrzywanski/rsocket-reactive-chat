package io.mkrzywanski.chat.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.UUID;

import static com.mongodb.client.model.changestream.OperationType.INSERT;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
class NewMessageWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewMessageWatcher.class);
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    NewMessageWatcher(final ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Flux<Message> newMessagesForChats(final Set<UUID> chats, final String username) {
        return reactiveMongoTemplate.changeStream(MessageDocument.class)
                .watchCollection("messages")
                .filter(where("chatRoomId").in(chats).and("usernameFrom").ne(username))
                .listen()
                .doOnNext(e -> LOGGER.info("event " + e))
                .filter(event -> event.getOperationType() == INSERT)
                .map(ChangeStreamEvent::getBody)
                .map(messageDocument -> new Message(messageDocument.getUsernameFrom(), messageDocument.getContent(), messageDocument.getChatRoomId(), "aaa"));
    }
}
