package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.message.api.Message;
import io.mkrzywanski.chat.app.message.api.Page;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Controller
class MessageReadController {

    private final MessageService messageService;

    MessageReadController(final MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("chat.{chatId}.messages")
    public Flux<Message> getMessagesForChatUnpaged(@DestinationVariable("chatId") final UUID chatId) {
        return messageService.findByChatId(chatId);
    }

    @MessageMapping("chat.{chatId}.messages.single")
    public Mono<List<Message>> getMessagesForChatAsSingle(@DestinationVariable("chatId") final UUID chatId) {
        return messageService.findByChatId(chatId).collectList();
    }

    @MessageMapping("chat.{chatId}.messages.paged")
    public Flux<Message> getMessagesForChatPaged(@DestinationVariable("chatId") final UUID chatId, final Page page) {
        return messageService.findByChatId(chatId, page);
    }

    @MessageMapping("chat.{chatId}.messages.paged.single")
    public Mono<List<Message>> getMessagesForChatPagedSingle(@DestinationVariable("chatId") final UUID chatId, final Page page) {
        return messageService.findByChatId(chatId, page).collectList();
    }
}
