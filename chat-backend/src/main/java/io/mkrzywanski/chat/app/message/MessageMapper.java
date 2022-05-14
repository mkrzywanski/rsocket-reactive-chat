package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.message.api.Message;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MessageMapper {
    static Message fromMessageDocument(final MessageDocument messageDocument) {
        return new Message(messageDocument.getUsernameFrom(),
                messageDocument.getContent(),
                messageDocument.getChatRoomId(),
                messageDocument.getTimestamp()
        );
    }
}
