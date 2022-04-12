package io.mkrzywanski.chat.app;

import java.util.UUID;

record Message(String usernameFrom, String content, UUID chatRoomId, String userName) {
}
