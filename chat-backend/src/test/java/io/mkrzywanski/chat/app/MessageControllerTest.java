package io.mkrzywanski.chat.app;

import io.mkrzywanski.chat.ChatApplication;
import io.rsocket.metadata.WellKnownMimeType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ChatApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = "spring.rsocket.server.port=0")
class MessageControllerTest {

    private static final MimeType SIMPLE_AUTH = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
    public static final String USER_1 = "user1";

    private RSocketRequester requesterUser1;
    private RSocketRequester requesterUser2;

    @Autowired
    private RSocketRequester.Builder builder;

    @LocalRSocketServerPort
    int port;

    @Autowired
    private MongoChatToUserMappingsHolder chatRoomUserMappings;

    @Autowired
    private MessageRepository messageRepository;

    @BeforeAll
    public void setupOnce() {
        requesterUser1 = setupUser1Requester();
        requesterUser2 = setupUser2Requester();
    }

    @AfterEach
    void tearDown() {
        chatRoomUserMappings.clear();
        messageRepository.deleteAll();
    }

    private RSocketRequester setupUser2Requester() {
        var user2 = new UsernamePasswordMetadata("user2", "pass");
        return setupRequesterFor(user2);
    }

    private RSocketRequester setupUser1Requester() {
        var user1 = new UsernamePasswordMetadata("user1", "pass");
        return setupRequesterFor(user1);
    }

    private RSocketRequester setupRequesterFor(UsernamePasswordMetadata usernamePasswordMetadata) {
        return builder
                .setupMetadata(usernamePasswordMetadata, SIMPLE_AUTH)
                .rsocketStrategies(v ->
                        v.encoder(new SimpleAuthenticationEncoder()))
                .tcp("localhost", port);
    }

    @Test
    void userCanCreateChat() {
        Mono<ChatCreatedResponse> result = requesterUser1
                .route("create-chat")
                .retrieveMono(ChatCreatedResponse.class);

        StepVerifier
                .create(result, 1)
                .consumeNextWith(message -> assertThat(message.chatId()).isNotNull())
                .verifyComplete();
    }

    @Test
    void userCanJoinExistingChat() {
        Mono<UUID> chatId = requesterUser1
                .route("create-chat")
                .retrieveMono(ChatCreatedResponse.class)
                .map(ChatCreatedResponse::chatId);

        Mono<Boolean> result = requesterUser2.route("join-chat")
                .data(new JoinChatRequest(chatId.block()))
                .retrieveMono(Boolean.class);

        StepVerifier
                .create(result)
                .consumeNextWith(message -> assertThat(message).isTrue())
                .verifyComplete();
    }

    @Test
    void user1ShouldGetMessagesFromUser2() {

        //user1 creates chat
        UUID chatId = requesterUser1
                .route("create-chat")
                .retrieveMono(ChatCreatedResponse.class)
                .map(ChatCreatedResponse::chatId)
                .block();

        //user2 joins chat
        Boolean joiningResult = requesterUser2.route("join-chat")
                .data(new JoinChatRequest(chatId))
                .retrieveMono(Boolean.class)
                .block();

        assert Boolean.TRUE.equals(joiningResult);

        //user1 wants to send this message
        Flux<Message> messageFromUser1 = Flux.just(new Message("user1", "hello from user1", chatId));

        //sends user 1 messages and awaits for messages from chats that this user is part of
        Flux<Message> incomingMessagesForUser1 = requesterUser1
                .route("chat-channel")
                .data(messageFromUser1)
                .retrieveFlux(Message.class);

        //user2 message
        Flux<Message> messagesFromUser2 = Flux.just(new Message("user2", "hello from user2", chatId));

        //sends user 1 messages and awaits for messages from chats that this user is part of
        Flux<Message> incomingMessagesForUser2 = requesterUser2
                .route("chat-channel")
                .data(messagesFromUser2)
                .retrieveFlux(Message.class);

        //subscribe to user1 flux and delay it by 1 second so that user2 channel can send messages and user1 receive them
        Disposable subscription = incomingMessagesForUser1
                .subscribeOn(Schedulers.boundedElastic())
                .delaySubscription(Duration.ofSeconds(1))
                .subscribe();

        StepVerifier
                .create(incomingMessagesForUser2, 1)
                .consumeNextWith(message -> {
                    assertThat(message.usernameFrom()).isEqualTo("user1");
                    assertThat(message.content()).isEqualTo("hello from user1");
                    assertThat(message.chatRoomId()).isEqualTo(chatId);
                })
                .thenCancel()
                .verify();

        subscription.dispose();
    }

    @Test
    void userShouldReceiveMessagesThatArriveOnChatThatHeJoinedAfterOpeningTheChannel() {
        //user1 creates first chat
        UUID firstChat = requesterUser1
                .route("create-chat")
                .retrieveMono(ChatCreatedResponse.class)
                .map(ChatCreatedResponse::chatId)
                .block();

        //user2 joins first chat
        Boolean joiningResult = requesterUser2.route("join-chat")
                .data(new JoinChatRequest(firstChat))
                .retrieveMono(Boolean.class)
                .block();

        assert Boolean.TRUE.equals(joiningResult);

        //user1 sink so that we can push to flux on demand
        Sinks.Many<Message> user1Sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Message> messageFromUser1 = user1Sink.asFlux();

        //sends user 1 messages and awaits for messages from chats that this user is part of
        Flux<Message> incomingMessagesForUser1 = requesterUser1
                .route("chat-channel")
                .data(messageFromUser1)
                .retrieveFlux(Message.class);

        //sends user 1 messages and awaits for messages from chats that this user is part of
        Flux<Message> incomingMessagesForUser2 = requesterUser2
                .route("chat-channel")
                .data(Flux.empty())
                .retrieveFlux(Message.class);


        //subscribe to user1 flux and delay it by 1 second so that user2 channel can send messages and user1 receive them
        Disposable subscription = incomingMessagesForUser1
                .subscribeOn(Schedulers.boundedElastic())
                .delaySubscription(Duration.ofSeconds(1))
                .subscribe();

        AtomicReference<UUID> secondChat = new AtomicReference<>();

        StepVerifier
                .create(incomingMessagesForUser2)
                .then(() -> user1Sink.emitNext(new Message(USER_1, "hello from user1", firstChat), Sinks.EmitFailureHandler.FAIL_FAST))
                .consumeNextWith(message -> {
                    assertThat(message.usernameFrom()).isEqualTo(USER_1);
                    assertThat(message.content()).isEqualTo("hello from user1");
                    assertThat(message.chatRoomId()).isEqualTo(firstChat);
                }).then(() -> {
                    //user1 creates anotherChat
                    UUID chatId = requesterUser1
                            .route("create-chat")
                            .retrieveMono(ChatCreatedResponse.class)
                            .map(ChatCreatedResponse::chatId)
                            .block();
                    secondChat.set(chatId);

                    //user2 joins second chat
                    Boolean joiningSecondChatResult = requesterUser2.route("join-chat")
                            .data(new JoinChatRequest(chatId))
                            .retrieveMono(Boolean.class)
                            .block();
                    assert Boolean.TRUE.equals(joiningSecondChatResult);

                    //user1 sends message to another chat
                    user1Sink.emitNext(new Message("user1", "hello from user1 on another chat", secondChat.get()), Sinks.EmitFailureHandler.FAIL_FAST);

                })
                .consumeNextWith(message -> {
                    //user2 should receive message from user1 on second chat
                    assertThat(message.usernameFrom()).isEqualTo("user1");
                    assertThat(message.content()).isEqualTo("hello from user1 on another chat");
                    assertThat(message.chatRoomId()).isEqualTo(secondChat.get());
                })
                .thenCancel()
                .verify();

        subscription.dispose();
    }
}