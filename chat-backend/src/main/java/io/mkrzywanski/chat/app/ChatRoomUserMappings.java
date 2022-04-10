package io.mkrzywanski.chat.app;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class ChatRoomUserMappings {
    private final Map<String, Set<UUID>> userNameToChat = new HashMap<>();
    Map<UUID, Set<String>> chatToUsers = new HashMap<>();

    public boolean putUserToChat(String userName, UUID chatId) {

        userNameToChat.computeIfAbsent(userName, s -> new HashSet<>()).add(chatId);
        return chatToUsers.computeIfAbsent(chatId, s -> new HashSet<>()).add(userName);
    }

    public Set<UUID> getUserChatRooms(String userName) {
        return userNameToChat.get(userName);
    }

    public Set<String> getUserFriends(String userName) {
        Set<UUID> chats = userNameToChat.get(userName);

        return chatToUsers.entrySet().stream()
                .filter(e -> chats.contains(e.getKey()))
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toSet());
    }

    public Set<UUID> getUserChats(String userName) {
        return userNameToChat.getOrDefault(userName, Set.of());
    }

    public void clear() {
        userNameToChat.clear();
        chatToUsers.clear();
    }
}
