package io.mkrzywanski.chat.app.chats.resuming;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
interface UserResumeTokenRepository extends ReactiveMongoRepository<UserResumeTokenDocument, UUID> {
    Mono<UserResumeTokenDocument> findByUserName(String username);
    Mono<Long> deleteByUserName(String username);
}
