package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.message.api.Message;
import io.mkrzywanski.chat.app.message.api.Page;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Controller
class MessageReadController {

    private final MessageService messageService;

    MessageReadController(final MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("chat.{chatId}.messages")
    public Flux<Message> getUserChats(@DestinationVariable("chatId") final UUID chatId) {
        return messageService.findByChatId(chatId);
    }

    @MessageMapping("chat.{chatId}.messages.paged")
    public Flux<Message> getUserChats(@DestinationVariable("chatId") final UUID chatId, final Page page) {
        return messageService.findByChatId(chatId, page);
    }
}
