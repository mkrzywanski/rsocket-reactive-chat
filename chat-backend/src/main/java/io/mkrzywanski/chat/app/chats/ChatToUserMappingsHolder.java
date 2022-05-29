package io.mkrzywanski.chat.app.chats;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface ChatToUserMappingsHolder {
    Mono<Boolean> putUserToChat(Mono<String> userName, UUID chatId);

    Mono<Set<UUID>> getUserChatRooms(Mono<String> userName);

    Flux<UsernameToChatsDocument> clear();
}
