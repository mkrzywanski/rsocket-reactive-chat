package io.mkrzywanski.chat.app;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserResumeTokenRepository extends ReactiveMongoRepository<UserResumeTokenDocument, UUID> {
    Mono<UserResumeTokenDocument> findByUserName(String username);
    Mono<Boolean> deleteByUserName(String username);
}
