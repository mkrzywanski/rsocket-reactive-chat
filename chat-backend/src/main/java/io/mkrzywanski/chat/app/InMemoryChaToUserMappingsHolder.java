package io.mkrzywanski.chat.app;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
class InMemoryChaToUserMappingsHolder implements ChaToUserMappingsHolder {

    private final Map<String, Set<UUID>> userNameToChat = new HashMap<>();

    @Override
    public boolean putUserToChat(String userName, UUID chatId) {
        return userNameToChat.computeIfAbsent(userName, s -> new HashSet<>()).add(chatId);
    }

    @Override
    public Set<UUID> getUserChatRooms(String userName) {
        return userNameToChat.get(userName);
    }

    public void clear() {
        userNameToChat.clear();
    }
}
