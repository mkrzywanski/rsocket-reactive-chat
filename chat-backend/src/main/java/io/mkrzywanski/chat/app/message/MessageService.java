package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.message.api.Message;
import io.mkrzywanski.chat.app.message.api.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        return findByChatId(chatId, Pageable.unpaged());
    }

    @PreAuthorize("@permissionEvaluator.isUserPartOfChat(#chatId, authentication.principal.username)")
    Flux<Message> findByChatId(final UUID chatId, final Page page) {
        final var pageRequest = PageRequest.of(page.pageNumber(), page.pageSize())
                .withSort(Sort.by(Sort.Direction.DESC, "timestamp"));
        return findByChatId(chatId, pageRequest);
    }

    private Flux<Message> findByChatId(final UUID chatId, final Pageable page) {
        return messageRepository.findByChatRoomId(chatId, page)
                .map(MessageMapper::fromMessageDocument);
    }
}
