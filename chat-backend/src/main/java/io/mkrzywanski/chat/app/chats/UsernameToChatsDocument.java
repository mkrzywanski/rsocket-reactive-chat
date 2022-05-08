package io.mkrzywanski.chat.app.chats;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Document("userChats")
@Getter
class UsernameToChatsDocument {
    @Id
    private UUID id;
    @Indexed(unique = true)
    private String userName;
    private Set<UUID> chats;

    UsernameToChatsDocument(final String userName, final Set<UUID> chats) {
        this.userName = userName;
        this.chats = chats;
        this.id = UUID.randomUUID();
    }

    void addChat(final UUID chat) {
        chats.add(chat);
    }
}
