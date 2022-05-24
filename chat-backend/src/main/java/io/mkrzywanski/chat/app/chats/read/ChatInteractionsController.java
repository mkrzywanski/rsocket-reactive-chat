package io.mkrzywanski.chat.app.chats.read;

import io.mkrzywanski.chat.app.chats.ChatToUserMappingsHolder;
import io.mkrzywanski.chat.app.chats.api.ChatCreatedResponse;
import io.mkrzywanski.chat.app.chats.api.JoinChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public Mono<ChatCreatedResponse> createChat(final String join, @AuthenticationPrincipal final UserDetails user) {
        log.info("Creating new chat");
        final UUID chatId = UUID.randomUUID();
        return chatRoomUserMappings.putUserToChat(user.getUsername(), chatId)
                .log()
                .map(ignored -> new ChatCreatedResponse(chatId));
    }

    @MessageMapping("join-chat")
    public Mono<Boolean> joinChat(final JoinChatRequest joinChatRequest, @AuthenticationPrincipal final UserDetails user) {
        return chatRoomUserMappings.putUserToChat(user.getUsername(), joinChatRequest.chatId()).log();
    }
}
