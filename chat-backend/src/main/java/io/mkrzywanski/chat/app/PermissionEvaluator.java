package io.mkrzywanski.chat.app;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
class PermissionEvaluator {

    private final ChatToUserMappingsHolder chatToUserMappingsHolder;

    PermissionEvaluator(final ChatToUserMappingsHolder chatToUserMappingsHolder) {
        this.chatToUserMappingsHolder = chatToUserMappingsHolder;
    }

    //https://github.com/spring-projects/spring-security/issues/9401
    public boolean isUserPartOfChat(UUID chatId, String userName) {
        return chatToUserMappingsHolder.getUserChatRooms(userName)
                .map(uuids -> uuids.contains(chatId))
                .share()
//                .publishOn(Schedulers.boundedElastic())
                .block();
    }
}
