package io.mkrzywanski.chat.app.chats.resuming;

import lombok.Getter;
import org.bson.BsonTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document("userTokens")
@Getter
class UserResumeTokenDocument {
    @Id
    private UUID uuid;

    @Indexed(unique = true)
    private String userName;

    private BsonTimestamp tokenTimestamp;

    UserResumeTokenDocument(final String userName) {
        this.userName = userName;
        this.uuid = UUID.randomUUID();
        this.tokenTimestamp = new BsonTimestamp((int) Instant.now().getEpochSecond(), 0);
    }

    void setTokenTimestamp(final BsonTimestamp tokenTimestamp) {
        this.tokenTimestamp = tokenTimestamp;
    }
}
