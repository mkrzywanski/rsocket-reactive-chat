package io.mkrzywanski.chat.app;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

interface ChatToUserMappingsHolder {
    Mono<Boolean> putUserToChat(String userName, UUID chatId);

    Mono<Set<UUID>> getUserChatRooms(String userName);
}
