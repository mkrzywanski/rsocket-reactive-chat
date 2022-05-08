package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.chats.ChatToUserMappingsHolder;
import io.mkrzywanski.chat.app.chats.api.ChatCreatedResponse;
import io.mkrzywanski.chat.app.chats.api.JoinChatRequest;
import io.mkrzywanski.chat.app.message.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Controller
class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);
    private final ChatToUserMappingsHolder chatRoomUserMappings;
    private final MessageRepository messageRepository;
    private final NewMessageWatcher newMessageWatcher;

    MessageController(@Qualifier("mongoChatToUserMappingsHolder") final ChatToUserMappingsHolder chatRoomUserMappings,
                      final MessageRepository messageRepository,
                      final NewMessageWatcher newMessageWatcher) {
        this.chatRoomUserMappings = chatRoomUserMappings;
        this.messageRepository = messageRepository;
        this.newMessageWatcher = newMessageWatcher;
    }

    @MessageMapping("create-chat")
    public Mono<ChatCreatedResponse> createChat(final String join, @AuthenticationPrincipal final UserDetails user) {
        LOG.info("Creating new chat");
        final UUID chatId = UUID.randomUUID();
        return chatRoomUserMappings.putUserToChat(user.getUsername(), chatId)
                .log()
                .map(ignored -> new ChatCreatedResponse(chatId));
    }

    @MessageMapping("join-chat")
    public Mono<Boolean> joinChat(final JoinChatRequest joinChatRequest, @AuthenticationPrincipal final UserDetails user) {
        return chatRoomUserMappings.putUserToChat(user.getUsername(), joinChatRequest.chatId()).log();
    }

    @MessageMapping("chat-channel")
    public Flux<Message> handle(final Flux<Message> incomingMessages, @AuthenticationPrincipal final UserDetails user) {
        final var messages = incomingMessages.map(MessageMapper::toMessageDocument);
        final var incomingMessagesSubscription = messageRepository.saveAll(messages)
                .then()
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(subscription -> LOG.info("subscribing to user " + user.getUsername() + " input channel"))
                .subscribe();
        final var userChats = chatRoomUserMappings.getUserChatRooms(user.getUsername());
        return newMessageWatcher.newMessagesForChats(userChats, user.getUsername())
                .doOnNext(message -> LOG.info("Message reply {}", message))
                .doOnSubscribe(subscription -> LOG.info("Subscribing to watcher : " + user.getUsername()))
                .doOnCancel(() -> {
                    LOG.info("Cancelled");
                    incomingMessagesSubscription.dispose();
                })
                .doOnError(throwable -> LOG.error(throwable.getMessage()));
    }
}
