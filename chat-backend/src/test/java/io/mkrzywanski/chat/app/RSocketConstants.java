package io.mkrzywanski.chat.app;

import io.rsocket.metadata.WellKnownMimeType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RSocketConstants {
    static final MimeType SIMPLE_AUTH = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
}
