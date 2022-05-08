package io.mkrzywanski.chat.app.message.api;

import java.util.UUID;

public record Message(String usernameFrom, String content, UUID chatRoomId) {
}
