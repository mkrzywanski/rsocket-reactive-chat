package io.mkrzywanski.chat.app;

import lombok.Getter;
import org.bson.BsonTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("userTokens")
@Getter
class UserResumeTokenDocument {
    @Id
    private UUID uuid;

    @Indexed(unique = true)
    private String userName;

    private BsonTimestamp tokenTimestamp;

    void setTokenTimestamp(final BsonTimestamp tokenTimestamp) {
        this.tokenTimestamp = tokenTimestamp;
    }
}
