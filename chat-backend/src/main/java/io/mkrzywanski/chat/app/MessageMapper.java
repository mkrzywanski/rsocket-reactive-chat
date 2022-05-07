package io.mkrzywanski.chat.app;

class MessageMapper {
    static Message fromMessageDocument(MessageDocument messageDocument) {
        return new Message(messageDocument.getUsernameFrom(), messageDocument.getContent(), messageDocument.getChatRoomId());
    }

    static MessageDocument toMessageDocument(Message message) {
        return new MessageDocument(
                message.usernameFrom(),
                message.content(),
                message.chatRoomId()
        );
    }
}
