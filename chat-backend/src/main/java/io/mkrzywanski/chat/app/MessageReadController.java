package io.mkrzywanski.chat.app;

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
        return messageService.get(chatId);
    }
}
