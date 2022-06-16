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
    public Mono<Boolean> putUserToChat(final Mono<String> userName, final UUID chatId) {
        return userName.map(s -> userNameToChat.computeIfAbsent(s, z -> new HashSet<>()).add(chatId));
    }

    @Override
    public Mono<Set<UUID>> getUserChatRooms(final Mono<String> userName) {
        return userName.map(userNameToChat::get);
    }

    @Override
    public Flux<UsernameToChatsDocument> clear() {
        userNameToChat.clear();
        return Flux.empty();
    }
}
