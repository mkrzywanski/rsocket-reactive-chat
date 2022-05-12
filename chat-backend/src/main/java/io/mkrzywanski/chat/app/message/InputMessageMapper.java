package io.mkrzywanski.chat.app.message;

import io.mkrzywanski.chat.app.message.api.InputMessage;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
class InputMessageMapper {

    private final Clock clock;

    InputMessageMapper(final Clock clock) {
        this.clock = clock;
    }

    MessageDocument fromInput(final InputMessage inputMessage) {
        final Instant messageTime = clock.instant();
        return new MessageDocument(inputMessage.usernameFrom(), inputMessage.content(), inputMessage.chatRoomId(), messageTime);
    }
}
