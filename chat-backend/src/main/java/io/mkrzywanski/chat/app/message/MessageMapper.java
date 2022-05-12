package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.message.api.Message;

class MessageMapper {
    static Message fromMessageDocument(final MessageDocument messageDocument) {
        return new Message(messageDocument.getUsernameFrom(),
                messageDocument.getContent(),
                messageDocument.getChatRoomId(),
                messageDocument.getTimestamp()
        );
    }

//    static MessageDocument toMessageDocument(final Message message) {
//        return new MessageDocument(
//                message.usernameFrom(),
//                message.content(),
//                message.chatRoomId(),
//                instant);
//    }
}
