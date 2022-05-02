package io.mkrzywanski.chat.app;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

interface ChatToUserMappingsHolder {
    Mono<Boolean> putUserToChat(String userName, UUID chatId);
    Supplier<Mono<Set<UUID>>> getUserChatRooms(String userName);
}
