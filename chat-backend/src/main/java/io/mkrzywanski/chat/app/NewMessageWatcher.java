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
    private ReactiveMongoTemplate reactiveMongoTemplate;

    NewMessageWatcher(final ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Flux<Message> newMessagesForChats(final Set<UUID> chats, final String username) {
//        ChangeStreamOptions options = ChangeStreamOptions.builder()
//                .filter(Aggregation.newAggregation(MessageDocument.class,
//                        Aggregation.match(
//                                where("operationType").is("replace")
//                        )
//                )).returnFullDocumentOnUpdate().build();

        return reactiveMongoTemplate.changeStream(MessageDocument.class)
                .watchCollection("messages")
                .filter(where("chatRoomId").in(chats).and("usernameFrom").ne(username))
                .listen()
                .doOnNext(e -> System.out.println("event " + e))
                .filter(event -> event.getOperationType() == INSERT)
                .map(ChangeStreamEvent::getBody)
                .map(messageDocument -> new Message(messageDocument.getUsernameFrom(), messageDocument.getContent(), messageDocument.getChatRoomId()));

        // return a flux that watches the changestream and returns the full document
//        return reactiveMongoTemplate.changeStream("messages", options, MessageDocument.class)
//                .map(ChangeStreamEvent::getBody)
//                .filter(message -> chats.contains(message.getChatRoomId()))// filter to only return the team that matches the name
//                .map(messageDocument -> new Message(messageDocument.getUsernameFrom(), messageDocument.getContent(), messageDocument.getChatRoomId()))
//                .doOnError(throwable -> LOGGER.error("Error with the teams changestream event: " + throwable.getMessage(), throwable));
//    }
    }
}
