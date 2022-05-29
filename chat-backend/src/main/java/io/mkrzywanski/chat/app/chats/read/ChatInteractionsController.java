package io.mkrzywanski.chat.app.chats.read;

import io.mkrzywanski.chat.app.chats.ChatToUserMappingsHolder;
import io.mkrzywanski.chat.app.chats.api.ChatCreatedResponse;
import io.mkrzywanski.chat.app.chats.api.JoinChatRequest;
import io.mkrzywanski.chat.app.common.Jwtutil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
@Slf4j
class ChatInteractionsController {

    private final ChatToUserMappingsHolder chatRoomUserMappings;

    ChatInteractionsController(final ChatToUserMappingsHolder chatRoomUserMappings) {
        this.chatRoomUserMappings = chatRoomUserMappings;
    }

    @MessageMapping("create-chat")
    public Mono<ChatCreatedResponse> createChat(final String join, @AuthenticationPrincipal final Mono<Jwt> jwtMono) {
        log.info("Creating new chat");
        final UUID chatId = UUID.randomUUID();
        return chatRoomUserMappings.putUserToChat(jwtMono.map(Jwtutil::extractUserName).doOnNext(s -> log.info("username {}", s)), chatId)
                .log()
                .map(ignored -> new ChatCreatedResponse(chatId))
                .doOnNext(chatCreatedResponse -> log.info(chatCreatedResponse.chatId().toString()));
    }

    @MessageMapping("join-chat")
    public Mono<Boolean> joinChat(final JoinChatRequest joinChatRequest, final @AuthenticationPrincipal Mono<Jwt> jwtMono) {
        return chatRoomUserMappings.putUserToChat(jwtMono.map(Jwtutil::extractUserName), joinChatRequest.chatId()).log();
    }
}
