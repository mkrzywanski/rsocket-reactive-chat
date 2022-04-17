package io.mkrzywanski.chat.app;

import lombok.extern.slf4j.Slf4j;
import org.bson.BsonTimestamp;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Clock;

@Component
@Slf4j
class UserResumeTokenService {

    private final UserResumeTokenRepository userResumeTokenRepository;
    private final Clock clock;

    UserResumeTokenService(final UserResumeTokenRepository userResumeTokenRepository, final Clock clock) {
        this.userResumeTokenRepository = userResumeTokenRepository;
        this.clock = clock;
    }

    void saveAndGenerateNewTokenFor(final String userName) {
        log.info("Saving token for user {}", userName);
        final var map = userResumeTokenRepository.findByUserName(userName).map(userResumeTokenDocument -> {
            final long epochSecond = clock.instant().getEpochSecond();
            userResumeTokenDocument.setTokenTimestamp(new BsonTimestamp((int) epochSecond, 0));
            return userResumeTokenDocument;
        });
        userResumeTokenRepository.saveAll(map);
    }

    Mono<BsonTimestamp> getResumeTimestampFor(final String userName) {
        return userResumeTokenRepository.findByUserName(userName)
                .map(UserResumeTokenDocument::getTokenTimestamp)
                .defaultIfEmpty(new BsonTimestamp((int) clock.instant().getEpochSecond(), 0));
//                .defaultIfEmpty(clock.instant());
    }

    void deleteTokenForUser(final String username) {
        userResumeTokenRepository.deleteByUserName(username);
        log.info("Token for user {} deleted", username);
    }
}
