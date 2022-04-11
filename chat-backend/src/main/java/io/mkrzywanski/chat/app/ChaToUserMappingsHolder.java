package io.mkrzywanski.chat.app;

import java.util.Set;
import java.util.UUID;

public interface ChaToUserMappingsHolder {
    boolean putUserToChat(String userName, UUID chatId);

    Set<UUID> getUserChatRooms(String userName);
}
