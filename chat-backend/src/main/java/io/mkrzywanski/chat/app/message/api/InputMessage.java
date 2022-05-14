package io.mkrzywanski.chat.app.message.api;

import java.util.UUID;

public record InputMessage(String usernameFrom, String content, UUID chatRoomId) {
}
