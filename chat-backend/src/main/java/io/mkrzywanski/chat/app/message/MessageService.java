package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.message.api.Message;
import io.mkrzywanski.chat.app.message.api.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    Flux<Message> findByChatId(final UUID chatId) {
        return messageRepository.findByChatRoomId(chatId, byTimestampDesc())
                .map(MessageMapper::fromMessageDocument);
    }

    @PreAuthorize("@permissionEvaluator.isUserPartOfChat(#chatId, authentication.principal.username)")
    Flux<Message> findByChatId(final UUID chatId, final Page page) {
        final var pageRequest = PageRequest.of(page.pageNumber(), page.pageSize())
                .withSort(byTimestampDesc());
        return messageRepository.findByChatRoomId(chatId, pageRequest)
                .map(MessageMapper::fromMessageDocument);
    }

    private Sort byTimestampDesc() {
        return Sort.by(Sort.Direction.DESC, "timestamp");
    }
}
