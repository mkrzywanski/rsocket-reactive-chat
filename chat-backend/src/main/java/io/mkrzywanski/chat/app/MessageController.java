package io.mkrzywanski.chat.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.UUID;

@Controller
class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);
    private final ChatRoomUserMappings chatRoomUserMappings;
    private final MessageRepository messageRepository;
    private final NewMessageWatcher newMessageWatcher;

    MessageController(final ChatRoomUserMappings chatRoomUserMappings, final MessageRepository messageRepository, final NewMessageWatcher newMessageWatcher) {
        this.chatRoomUserMappings = chatRoomUserMappings;
        this.messageRepository = messageRepository;
        this.newMessageWatcher = newMessageWatcher;
    }

    @MessageMapping("create-chat")
    public Mono<ChatCreatedResponse> createChat(@AuthenticationPrincipal UserDetails user) {
        UUID chatId = UUID.randomUUID();
        chatRoomUserMappings.putUserToChat(user.getUsername(), chatId);
        return Mono.just(new ChatCreatedResponse(chatId));
    }

    @MessageMapping("join-chat")
    public Mono<Boolean> joinChat(JoinChatRequest joinChatRequest, @AuthenticationPrincipal UserDetails user) {
        boolean b = chatRoomUserMappings.putUserToChat(user.getUsername(), joinChatRequest.chatId());
        return Mono.just(b);
    }

    @MessageMapping("chat-channel")
    public Flux<Message> handle(Flux<Message> incomingMessages, @AuthenticationPrincipal UserDetails user) {
        Flux<MessageDocument> map = incomingMessages.map(this::toMessageDocument);
        Disposable voidMono = messageRepository.saveAll(map)
                .doOnNext(messageDocument -> LOG.info("AAAA {}", messageDocument.toString()))
                .then()
                .subscribeOn(Schedulers.boundedElastic()).subscribe();
        Set<UUID> userChats = chatRoomUserMappings.getUserChatRooms(user.getUsername());
        return newMessageWatcher.newMessagesForChats(userChats, user.getUsername())
                .doOnNext(message -> LOG.info("Message reply {}", message))
                .doOnSubscribe(subscription -> {
                    LOG.info("Subscribing to watcher");
                })
                .doOnCancel(() -> {
                    LOG.info("Cancelled");
                    voidMono.dispose();
                }).doOnError(throwable -> {
                    LOG.error(throwable.getMessage());
                });
    }

    private MessageDocument toMessageDocument(Message message) {
        return new MessageDocument(
                message.usernameFrom(),
                message.content(),
                message.chatRoomId()
        );
    }
}
