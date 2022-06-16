package io.mkrzywanski.chat.app.chats.resuming;

import lombok.extern.slf4j.Slf4j;
import org.bson.BsonTimestamp;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Clock;
import java.util.function.Function;

@Slf4j
@Component
public class UserResumeTokenService {

    private final UserResumeTokenRepository userResumeTokenRepository;
    private final Clock clock;

    UserResumeTokenService(final UserResumeTokenRepository userResumeTokenRepository, final Clock clock) {
        this.userResumeTokenRepository = userResumeTokenRepository;
        this.clock = clock;
    }

    public void saveAndGenerateNewTokenFor(final String userName) {
        log.info("Saving token for user {}", userName);
        final var userToken = userResumeTokenRepository.findByUserName(userName)
                .defaultIfEmpty(new UserResumeTokenDocument(userName))
                .map(changeCurrentToken());
        userResumeTokenRepository.saveAll(userToken)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnComplete(() -> log.info("User {} token saved ", userName))
                .subscribe();
    }

    private Function<UserResumeTokenDocument, UserResumeTokenDocument> changeCurrentToken() {
        return userResumeTokenDocument -> {
            final long epochSecond = clock.instant().getEpochSecond();
            userResumeTokenDocument.setTokenTimestamp(new BsonTimestamp((int) epochSecond, 0));
            return userResumeTokenDocument;
        };
    }

    public Mono<BsonTimestamp> getResumeTimestampFor(final Mono<String> userNameMono) {
        return userNameMono.flatMap(userName -> userResumeTokenRepository.findByUserName(userName)
                .map(UserResumeTokenDocument::getTokenTimestamp)
                .defaultIfEmpty(new BsonTimestamp((int) clock.instant().getEpochSecond(), 0)));

    }

    public Mono<Boolean> deleteTokenForUser(final String username) {
        return userResumeTokenRepository.deleteByUserName(username)
                .map(deletedCount -> true)
                .doOnSuccess(ignored -> log.info("Token for user {} deleted", username));
    }
}
