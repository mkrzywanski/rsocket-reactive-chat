package io.mkrzywanski.chat.app;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("messages")
class MessageDocument {
    @Id
    private UUID id;
    private String usernameFrom;
    private String content;
    private UUID chatRoomId;

    MessageDocument(final String usernameFrom, final String content, final UUID chatRoomId) {
        this.id = UUID.randomUUID();
        this.usernameFrom = usernameFrom;
        this.content = content;
        this.chatRoomId = chatRoomId;
    }

    UUID getId() {
        return id;
    }

    String getUsernameFrom() {
        return usernameFrom;
    }

    String getContent() {
        return content;
    }

    UUID getChatRoomId() {
        return chatRoomId;
    }

    boolean isNotFromUser(String usernameFrom) {
        return !this.usernameFrom.equals(usernameFrom);
    }

    @Override
    public String toString() {
        return "MessageDocument{" +
                "id=" + id +
                ", usernameFrom='" + usernameFrom + '\'' +
                ", content='" + content + '\'' +
                ", chatRoomId=" + chatRoomId +
                '}';
    }
}
