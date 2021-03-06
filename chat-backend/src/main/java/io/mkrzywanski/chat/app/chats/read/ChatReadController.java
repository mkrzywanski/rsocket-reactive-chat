package io.mkrzywanski.chat.app.chats.read;

import io.mkrzywanski.chat.app.chats.ChatToUserMappingsHolder;
import io.mkrzywanski.chat.app.common.JwtUtil;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Controller
class ChatReadController {

    private final ChatToUserMappingsHolder chatToUserMappingsHolder;

    ChatReadController(final ChatToUserMappingsHolder chatToUserMappingsHolder) {
        this.chatToUserMappingsHolder = chatToUserMappingsHolder;
    }

    @MessageMapping("get-user-chats")
    public Mono<Set<UUID>> getUserChats(@AuthenticationPrincipal final Mono<Jwt> jwtMono) {
        return chatToUserMappingsHolder.getUserChatRooms(jwtMono.map(JwtUtil::extractUserName));
    }
}

