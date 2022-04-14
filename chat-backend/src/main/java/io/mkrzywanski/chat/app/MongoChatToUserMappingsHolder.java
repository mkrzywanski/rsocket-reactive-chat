package io.mkrzywanski.chat.app;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
class MongoChatToUserMappingsHolder implements ChatToUserMappingsHolder {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    MongoChatToUserMappingsHolder(final ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public boolean putUserToChat(final String userName, final UUID chatId) {
        final var map = reactiveMongoTemplate.findById(userName, UsernameToChatsDocument.class)
                .defaultIfEmpty(new UsernameToChatsDocument(userName, Set.of(chatId)))
                .map(usernameToChatsDocument -> {
                    usernameToChatsDocument.addChat(chatId);
                    return usernameToChatsDocument;
                });
        reactiveMongoTemplate.save(map);
        return true;
    }

    @Override
    public Supplier<Mono<Set<UUID>>> getUserChatRooms(final String userName) {
        Query query = Query.query(Criteria.where("userName").is(userName));
        return () -> reactiveMongoTemplate.find(query, UsernameToChatsDocument.class)
                .flatMapIterable(UsernameToChatsDocument::getChats)
                .collect(Collectors.toSet())
                .cache();
    }

    void clear() {
        reactiveMongoTemplate.remove(UsernameToChatsDocument.class);
    }
}
