package io.mkrzywanski.chat.app;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Controller
@MessageMapping("get.")
class MessageReadController {

    private final ChatToUserMappingsHolder chatToUserMappingsHolder;

    MessageReadController(final ChatToUserMappingsHolder chatToUserMappingsHolder) {
        this.chatToUserMappingsHolder = chatToUserMappingsHolder;
    }

    @MessageMapping("user.chats")
    public Mono<Set<UUID>> getUserChats(@AuthenticationPrincipal final UserDetails user) {
        return chatToUserMappingsHolder.getUserChatRooms(user.getUsername()).get();
    }
}

