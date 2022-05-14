package io.mkrzywanski.chat.app.infra;

import io.mkrzywanski.chat.app.chats.ChatToUserMappingsHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class PermissionEvaluator {

    private final ChatToUserMappingsHolder chatToUserMappingsHolder;

    PermissionEvaluator(final ChatToUserMappingsHolder chatToUserMappingsHolder) {
        this.chatToUserMappingsHolder = chatToUserMappingsHolder;
    }

    //https://github.com/spring-projects/spring-security/issues/9401
    public boolean isUserPartOfChat(final UUID chatId, final String userName) {
        return chatToUserMappingsHolder.getUserChatRooms(userName)
                .map(uuids -> uuids.contains(chatId))
                .share()
                .block();
    }
}
