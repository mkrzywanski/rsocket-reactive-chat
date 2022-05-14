package io.mkrzywanski.chat.app.chats;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface ChatToUserMappingsHolder {
    Mono<Boolean> putUserToChat(String userName, UUID chatId);

    Mono<Set<UUID>> getUserChatRooms(String userName);

    Flux<UsernameToChatsDocument> clear();
}
