package io.mkrzywanski.chat.app;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Document("userChats")
class UsernameToChatsDocument {
    @Id
    private UUID id;
    private String userName;
    private Set<UUID> chats;
}
