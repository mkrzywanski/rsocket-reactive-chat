package io.mkrzywanski.chat.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
class MongoChatToUserMappingsHolder implements ChatToUserMappingsHolder {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    MongoChatToUserMappingsHolder(final ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<Boolean> putUserToChat(final String userName, final UUID chatId) {
        final Query query = Query.query(Criteria.where("userName").is(userName));
        final var document = reactiveMongoTemplate.findOne(query, UsernameToChatsDocument.class)
                .defaultIfEmpty(new UsernameToChatsDocument(userName, new HashSet<>()))
                .map(usernameToChatsDocument -> {
                    usernameToChatsDocument.addChat(chatId);
                    return usernameToChatsDocument;
                });
        return reactiveMongoTemplate.save(document)
                .doOnNext(usernameToChatsDocument -> log.info("saving document {}", usernameToChatsDocument))
                .map(usernameToChatsDocument -> true);
    }

    @Override
    public Mono<Set<UUID>> getUserChatRooms(final String userName) {
        final Query query = Query.query(Criteria.where("userName").is(userName));
        return reactiveMongoTemplate.findOne(query, UsernameToChatsDocument.class)
                .doOnNext(usernameToChatsDocument -> log.info("found user {}", usernameToChatsDocument.getUserName()))
                .flatMapIterable(UsernameToChatsDocument::getChats)
                .collect(Collectors.toSet())
                .doOnNext(uuids -> log.info("set size {}", uuids.size()))
                .defaultIfEmpty(Set.of())
                .doOnNext(uuids -> log.info("set size {}", uuids.size()));
    }

    Flux<UsernameToChatsDocument> clear() {
        return reactiveMongoTemplate.remove(UsernameToChatsDocument.class)
                .findAndRemove();
    }
}
