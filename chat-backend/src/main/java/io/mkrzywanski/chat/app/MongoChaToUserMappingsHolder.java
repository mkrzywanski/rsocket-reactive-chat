package io.mkrzywanski.chat.app;

import java.util.Set;
import java.util.UUID;

class MongoChaToUserMappingsHolder implements ChaToUserMappingsHolder {
    @Override
    public boolean putUserToChat(final String userName, final UUID chatId) {
        return false;
    }

    @Override
    public Set<UUID> getUserChatRooms(final String userName) {
        return null;
    }
}
