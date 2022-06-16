package io.mkrzywanski.chat.app.chats;

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
    public Mono<Boolean> putUserToChat(final Mono<String> userNameMono, final UUID chatId) {
        return userNameMono.flatMap(s -> {
            final Query userName = Query.query(Criteria.where("userName").is(s));
            final var document = reactiveMongoTemplate.findOne(userName, UsernameToChatsDocument.class)
                    .defaultIfEmpty(new UsernameToChatsDocument(s, new HashSet<>()))
                    .map(usernameToChatsDocument -> {
                        usernameToChatsDocument.addChat(chatId);
                        return usernameToChatsDocument;
                    });
            return reactiveMongoTemplate.save(document)
                    .doOnNext(usernameToChatsDocument -> log.info("saving document {}", usernameToChatsDocument))
                    .map(usernameToChatsDocument -> true);
        });
    }

    @Override
    public Mono<Set<UUID>> getUserChatRooms(final Mono<String> userNameMono) {
        return userNameMono.map(username -> Query.query(Criteria.where("userName").is(username)))
                .flatMap(query -> reactiveMongoTemplate.findOne(query, UsernameToChatsDocument.class)
                        .doOnNext(usernameToChatsDocument -> log.info("found user {}", usernameToChatsDocument.getUserName()))
                        .flatMapIterable(UsernameToChatsDocument::getChats)
                        .collect(Collectors.toSet())
                        .doOnNext(uuids -> log.info("set size {}", uuids.size()))
                        .defaultIfEmpty(Set.of())
                        .doOnNext(uuids -> log.info("set size {}", uuids.size())));
    }

    @Override
    public Flux<UsernameToChatsDocument> clear() {
        return reactiveMongoTemplate.remove(UsernameToChatsDocument.class)
                .findAndRemove();
    }
}
