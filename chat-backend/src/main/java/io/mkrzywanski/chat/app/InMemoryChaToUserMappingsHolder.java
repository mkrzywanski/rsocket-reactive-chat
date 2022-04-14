package io.mkrzywanski.chat.app;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

class InMemoryChaToUserMappingsHolder implements ChatToUserMappingsHolder {

    private final Map<String, Set<UUID>> userNameToChat = new ConcurrentHashMap<>();

    @Override
    public boolean putUserToChat(String userName, UUID chatId) {
        return userNameToChat.computeIfAbsent(userName, s -> new HashSet<>()).add(chatId);
    }

    @Override
    public Supplier<Mono<Set<UUID>>> getUserChatRooms(String userName) {
        return () -> Mono.just(userNameToChat.get(userName));
    }

    public void clear() {
        userNameToChat.clear();
    }
}
