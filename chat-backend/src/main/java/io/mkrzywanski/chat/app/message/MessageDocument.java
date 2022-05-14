package io.mkrzywanski.chat.app.message;

import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Document("messages")
@ToString
class MessageDocument {

    @Id
    private UUID id;
    private String usernameFrom;
    private String content;
    private UUID chatRoomId;
    private Instant timestamp;

    MessageDocument(final String usernameFrom, final String content, final UUID chatRoomId, final Instant timestamp) {
        this.timestamp = timestamp;
        this.id = UUID.randomUUID();
        this.usernameFrom = usernameFrom;
        this.content = content;
        this.chatRoomId = chatRoomId;
    }

    boolean isNotFromUser(final String usernameFrom) {
        return !this.usernameFrom.equals(usernameFrom);
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

    Instant getTimestamp() {
        return timestamp.truncatedTo(ChronoUnit.MILLIS);
    }
}
