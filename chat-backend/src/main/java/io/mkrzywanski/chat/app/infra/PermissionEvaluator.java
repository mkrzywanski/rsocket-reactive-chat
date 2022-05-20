package io.mkrzywanski.chat.app.infra;

import io.mkrzywanski.chat.app.chats.ChatToUserMappingsHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
class PermissionEvaluator {

    private final ChatToUserMappingsHolder chatToUserMappingsHolder;

    PermissionEvaluator(final ChatToUserMappingsHolder chatToUserMappingsHolder) {
        this.chatToUserMappingsHolder = chatToUserMappingsHolder;
    }

    //https://github.com/spring-projects/spring-security/issues/9401
    public boolean isUserPartOfChat(final UUID chatId, final String userName) {
        final var isAuthorized = chatToUserMappingsHolder.getUserChatRooms(userName)
                .map(uuids -> uuids.contains(chatId))
                .share()
                .block();
        log.info("User {} is authorized : {}", userName, isAuthorized);
        return isAuthorized;
    }
}
