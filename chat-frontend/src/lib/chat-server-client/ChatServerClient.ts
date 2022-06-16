import {
  BufferEncoders,
  encodeCompositeMetadata,
  encodeRoute,
  MESSAGE_RSOCKET_AUTHENTICATION,
  MESSAGE_RSOCKET_COMPOSITE_METADATA,
  MESSAGE_RSOCKET_ROUTING,
  RSocketClient,
} from "rsocket-core";
import { Payload, ReactiveSocket } from "rsocket-types";
import RSocketWebsocketClient from "rsocket-websocket-client";
import { InputMessage } from "../api/InputMessage";
import { JoinChatRequest } from "../api/JoinChatRequest";
import { Message } from "../api/Message";
import { Page } from "../api/Page";
import { AuthMetadataProvider } from "../auth/AuthMetadataProvider";
import { MessageStreamSubscriber } from "./MessageStreamSubscriber";

class ChatServerClient {
  private readonly host: String;
  private readonly port: number;
  private rsocket!: ReactiveSocket<any, any>;

  private constructor(host: String = "localhost", port: number = 9090) {
    this.host = host;
    this.port = port;
  }

  private async createClient() {
    const client = new RSocketClient({
      // serializers: {
      //     data: JsonSerializer,
      //     metadata: IdentitySerializer
      // },
      setup: {
        dataMimeType: "application/json",
        keepAlive: 1000000, // avoid sending during test
        lifetime: 100000,
        metadataMimeType: MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
      },
      transport: new RSocketWebsocketClient(
        {
          debug: true,
          url: "ws://" + this.host + ":" + this.port,
          wsCreator: (url) => {
            return new WebSocket(url);
          },
        },
        BufferEncoders
      ),
      errorHandler: (e) => {
        console.log(e);
      },
    });
    return await client.connect();
  }

  public static CreateAsync = async (
    host: String = "localhost",
    port: number = 9090
  ) => {
    const client = new ChatServerClient(host, port);
    client.rsocket = await client.createClient();
    return client;
  };

  createChat(
    userMetadataProvider: AuthMetadataProvider,
    onComplete: (param: string) => void
  ) {
    const metadata = encodeCompositeMetadata([
      [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("create-chat")],
      [
        MESSAGE_RSOCKET_AUTHENTICATION.string,
        userMetadataProvider.userMetadata(),
      ],
    ]);
    this.rsocket
      .requestResponse({
        // data: Buffer.from("aaa"),
        data: Buffer.from(JSON.stringify({ a: "a" })),
        metadata: metadata,
      })
      .subscribe({
        onComplete: (data) => {
          const response = JSON.parse(data.data);
          onComplete(response.chatId);
        },
        onError: (error) => {
          console.log(error + " error");
        },
        onSubscribe: (cancel) => {
        },
      });
  }

  joinChat(
    userMetadataProvider: AuthMetadataProvider,
    chatId: String,
    onComplete: (chat: boolean) => void
  ) {
    const metadata = encodeCompositeMetadata([
      [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("join-chat")],
      [
        MESSAGE_RSOCKET_AUTHENTICATION.string,
        userMetadataProvider.userMetadata(),
      ],
    ]);
    this.rsocket
      .requestResponse({
        data: Buffer.from(JSON.stringify(new JoinChatRequest(chatId))),
        metadata: metadata,
      })
      .subscribe({
        onComplete: (data: Payload<any, any>) => {
          const result: boolean = JSON.parse(data.data);
          onComplete(result);
        },
        onError: (error) => {
          console.log(error + " error");
        },
        onSubscribe: (cancel) => {
        },
      });
  }

  sendMessage(
    userMetadataProvider: AuthMetadataProvider,
    message: InputMessage,
    onComplete: (message: Message) => void
  ) {
    const metadata = encodeCompositeMetadata([
      [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("send-message")],
      [
        MESSAGE_RSOCKET_AUTHENTICATION.string,
        userMetadataProvider.userMetadata(),
      ],
    ]);
    this.rsocket
      .requestResponse({
        data: Buffer.from(JSON.stringify(message)),
        metadata: metadata,
      })
      .subscribe({
        onComplete: (data) => {
          const message: Message = JSON.parse(data.data);
          onComplete(message);
        },
        onError: (error) => {
          console.log(error + " error");
        },
        onSubscribe: (cancel) => {
        }
      });
  }

  messageStream(
    userMetadataProvider: AuthMetadataProvider,
    onNextMessage: (message: Message) => void
  ) {
    const metadata = encodeCompositeMetadata([
      [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("messages-stream")],
      [
        MESSAGE_RSOCKET_AUTHENTICATION.string,
        userMetadataProvider.userMetadata(),
      ],
    ]);
    this.rsocket
      .requestStream({
        metadata: metadata,
      })
      .subscribe(new MessageStreamSubscriber(onNextMessage));
  }

  getUserChats(
    userMetadataProvider: AuthMetadataProvider,
    onComplete: (chats: Set<string>) => void
  ) {
    const metadata = encodeCompositeMetadata([
      [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("get-user-chats")],
      [
        MESSAGE_RSOCKET_AUTHENTICATION.string,
        userMetadataProvider.userMetadata(),
      ],
    ]);
    this.rsocket
      .requestResponse({
        metadata: metadata,
      })
      .subscribe({
        onComplete: (data) => {
          const message: Set<string> = JSON.parse(data.data);
          onComplete(message);
        },
        onError: (error) => {
          console.log(error + " error");
        },
        onSubscribe: (cancel) => {
        },
      });
  }

  getMessagesForChatPaged(
    userMetadataProvider: AuthMetadataProvider,
    chatId: string,
    page: Page,
    onComplete: (messages: Message[]) => void
  ) {
    const metadata = encodeCompositeMetadata([
      [
        MESSAGE_RSOCKET_ROUTING.string,
        encodeRoute("chat." + chatId + ".messages.paged.single"),
      ],
      [
        MESSAGE_RSOCKET_AUTHENTICATION.string,
        userMetadataProvider.userMetadata(),
      ],
    ]);
    this.rsocket
      .requestResponse({
        data: Buffer.from(JSON.stringify(page)),
        metadata: metadata,
      })
      .subscribe({
        onComplete: (data) => {
          const messages: Message[] = JSON.parse(data.data);
          onComplete(messages);
        },
        onError: (error) => {
          console.log(error + " error");
        },
        onSubscribe: (cancel) => {
        },
      });
  }

  disconnect() {
    this.rsocket.close();
  }
}

export { ChatServerClient };
