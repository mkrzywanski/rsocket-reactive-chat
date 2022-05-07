package io.mkrzywanski.chat.app;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
class MessageService {

    private final MessageRepository messageRepository;

    MessageService(final MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @PreAuthorize("@permissionEvaluator.isUserPartOfChat(#chatId, authentication.principal.username)")
    Flux<Message> get(UUID chatId) {
        return messageRepository.findByChatRoomId(chatId)
                .map(MessageMapper::fromMessageDocument);
    }
}
