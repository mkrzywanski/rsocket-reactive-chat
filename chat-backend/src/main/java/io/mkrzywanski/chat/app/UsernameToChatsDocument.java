package io.mkrzywanski.chat.app;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Document("userChats")
@Getter
class UsernameToChatsDocument {
    @Id
    private UUID id;
    private String userName;
    private Set<UUID> chats;

    UsernameToChatsDocument(final String userName, final Set<UUID> chats) {
        this.userName = userName;
        this.chats = chats;
        this.id = UUID.randomUUID();
    }

    void addChat(UUID chat) {
        chats.add(chat);
    }
}
