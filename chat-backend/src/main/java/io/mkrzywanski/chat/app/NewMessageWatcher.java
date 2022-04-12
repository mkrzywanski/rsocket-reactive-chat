package io.mkrzywanski.chat.app;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.UUID;

@Component
class NewMessageWatcher {

//    private static final Logger LOGGER = LoggerFactory.getLogger(NewMessageWatcher.class);
//    private final ReactiveMongoTemplate reactiveMongoTemplate;
//
//    NewMessageWatcher(final ReactiveMongoTemplate reactiveMongoTemplate) {
//        this.reactiveMongoTemplate = reactiveMongoTemplate;
//    }

    public Flux<Message> newMessagesForChats(final Set<UUID> chats, final String username) {
        return Flux.empty();
//        return reactiveMongoTemplate.changeStream(MessageDocument.class)
//                .watchCollection("messages")
//                .filter(where("chatRoomId").in(chats).and("usernameFrom").ne(username))
//                .listen()
//                .doOnNext(e -> LOGGER.info("event " + e))
//                .filter(event -> event.getOperationType() == INSERT)
//                .map(ChangeStreamEvent::getBody)
//                .map(messageDocument -> new Message(messageDocument.getUsernameFrom(), messageDocument.getContent(), messageDocument.getChatRoomId(), "aaa"));
    }
}
