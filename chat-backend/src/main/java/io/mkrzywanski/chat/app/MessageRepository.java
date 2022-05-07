package io.mkrzywanski.chat.app;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
interface MessageRepository extends ReactiveMongoRepository<MessageDocument, UUID> {
    Flux<MessageDocument> findByChatRoomId(UUID chatRoomId);
}
