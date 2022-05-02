package io.mkrzywanski.chat.app;

import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

class InMemoryChatToUserMappingsHolder implements ChatToUserMappingsHolder {

    private final Map<String, Set<UUID>> userNameToChat = new ConcurrentHashMap<>();

    @Override
    public Mono<Boolean> putUserToChat(final String userName, final UUID chatId) {
        return Mono.just(userNameToChat.computeIfAbsent(userName, s -> new HashSet<>()).add(chatId));
    }

    @Override
    public Supplier<Mono<Set<UUID>>> getUserChatRooms(final String userName) {
        return () -> Mono.just(userNameToChat.get(userName));
    }

    public void clear() {
        userNameToChat.clear();
    }
}
