package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.message.api.Message;
import io.mkrzywanski.chat.app.chats.resuming.UserResumeTokenService;
import org.bson.BsonTimestamp;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static com.mongodb.client.model.changestream.OperationType.INSERT;

@Component
class NewMessageWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewMessageWatcher.class);

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final UserResumeTokenService resumeTokenService;

    NewMessageWatcher(final ReactiveMongoTemplate reactiveMongoTemplate,
                      final UserResumeTokenService userResumeTokenRepository) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.resumeTokenService = userResumeTokenRepository;
    }

    public Flux<Message> newMessagesForChats(final Mono<Set<UUID>> chats, final Mono<String> userNameMono) {
        return userNameMono.flatMapMany(userName -> resumeTokenService.getResumeTimestampFor(userNameMono)
                .flatMapMany(bsonTimestamp -> changeStream(userName, chats, bsonTimestamp))
                .doOnCancel(() -> resumeTokenService.saveAndGenerateNewTokenFor(userName))
        );

    }

    private Flux<Message> changeStream(final String username,
                                       final Mono<Set<UUID>> chats,
                                       final BsonTimestamp bsonTimestamp) {
        System.out.println("USERNAMEEEE " + username);
        final Function<MessageDocument, Publisher<Boolean>> messageIsForThisUserChat =
                message -> chats.map(chatIds -> {
                    LOGGER.info(" chatids " + chatIds);
                    return chatIds.contains(message.getChatRoomId());
                });
        return reactiveMongoTemplate.changeStream(MessageDocument.class)
                .watchCollection("messages")
                .resumeAt(bsonTimestamp)
                .listen()
                .doOnNext(e -> LOGGER.info(" xdd event {}", e))
                .filter(event -> event.getOperationType() == INSERT)
                .map(ChangeStreamEvent::getBody)
                .doOnNext(messageDocument -> LOGGER.info(messageDocument.toString()))
                .filter(m -> m.isNotFromUser(username))
                .filterWhen(messageIsForThisUserChat)
                .map(MessageMapper::fromMessageDocument);
    }
}
