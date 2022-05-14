package io.mkrzywanski.chat.app.message;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<MessageDocument, UUID> {
    Flux<MessageDocument> findByChatRoomId(UUID chatRoomId);
    Flux<MessageDocument> findByChatRoomId(UUID chatRoomId, Pageable pageable);
    Flux<MessageDocument> findByChatRoomId(UUID chatRoomId, Sort sort);
}
