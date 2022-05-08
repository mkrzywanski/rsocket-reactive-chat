package io.mkrzywanski.chat.app.chats;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryChatToUserMappingsHolder implements ChatToUserMappingsHolder {

    private final Map<String, Set<UUID>> userNameToChat = new ConcurrentHashMap<>();

    @Override
    public Mono<Boolean> putUserToChat(final String userName, final UUID chatId) {
        return Mono.just(userNameToChat.computeIfAbsent(userName, s -> new HashSet<>()).add(chatId));
    }

    @Override
    public Mono<Set<UUID>> getUserChatRooms(final String userName) {
        return Mono.just(userNameToChat.get(userName));
    }

    @Override
    public Flux<UsernameToChatsDocument> clear() {
        userNameToChat.clear();
        return Flux.empty();
    }
}
