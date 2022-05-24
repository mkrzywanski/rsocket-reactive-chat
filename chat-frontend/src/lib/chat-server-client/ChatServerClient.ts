
import { BufferEncoders, encodeCompositeMetadata, encodeRoute, IdentitySerializer, JsonSerializer, MESSAGE_RSOCKET_AUTHENTICATION, MESSAGE_RSOCKET_COMPOSITE_METADATA, MESSAGE_RSOCKET_ROUTING, RSocketClient, Utf8Encoders } from 'rsocket-core';
import { ReactiveSocket, ISubscription } from 'rsocket-types';
import RSocketWebsocketClient from 'rsocket-websocket-client';
import { AuthMetadataProvider } from './AuthMetadataProvider';
import { InputMessage } from './InputMessage';
import { JoinChatRequest } from './JoinChatRequest';
import { Message } from './Message';

class ChatServerClient {

    private readonly host: String;
    private readonly port: number;
    private rsocket!: ReactiveSocket<any, any>;

    private constructor(host: String = "localhost", port: number = 9090) {
        this.host = host;
        this.port = port
    }

    private async createClient() {
        const client = new RSocketClient(
            {
                // serializers: {
                //     data: JsonSerializer,
                //     metadata: IdentitySerializer
                // },
                setup: {
                    dataMimeType: 'application/json',
                    keepAlive: 1000000, // avoid sending during test
                    lifetime: 100000,
                    metadataMimeType: MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
                },
                transport: new RSocketWebsocketClient({
                    debug: true,
                    url: "ws://" + this.host + ":" + this.port,
                    wsCreator: (url) => {
                        return new WebSocket(url);
                    }
                }, BufferEncoders),
                errorHandler: (e) => {
                    console.log(e)

                }
            }
        );
        return await client.connect();
    }

    public static CreateAsync = async (host: String = "localhost", port: number = 9090) => {
        console.log("create client async")
        const client = new ChatServerClient(host, port);
        client.rsocket = await client.createClient()
        return client;
    };


    createChat(userMetadataProvider: AuthMetadataProvider, onComplete: (param: string) => void) {
        const metadata = encodeCompositeMetadata(
            [
                [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("create-chat")],
                [MESSAGE_RSOCKET_AUTHENTICATION.string, userMetadataProvider.userMetadata()]
            ]
        )
        this.rsocket.requestResponse({
            // data: Buffer.from("aaa"),
            data: Buffer.from(JSON.stringify({a: "a"})),
            metadata: metadata
        }).subscribe({
            onComplete: data => {
                const response = JSON. parse(data.data);
                onComplete(response.chatId)
                console.log('data ' + response.chatId)
            },
            onError: error => {
                console.log(error + ' error')
            },
            onSubscribe: cancel => {
                console.log('subscribe create chat')
                // console.log(cancel)
            }
        });
    }

    joinChat(userMetadataProvider: AuthMetadataProvider, chatId : String, onComplete : (c : boolean) => void) {
        const metadata = encodeCompositeMetadata(
            [
                [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("join-chat")],
                [MESSAGE_RSOCKET_AUTHENTICATION.string, userMetadataProvider.userMetadata()]
            ]
        )
        this.rsocket.requestResponse({
            // data: Buffer.from("aaa"),
            data: Buffer.from(JSON.stringify(new JoinChatRequest(chatId))),
            metadata: metadata
        }).subscribe({
            onComplete: data => {
                onComplete(data.data)
                console.log('data joint chat' + data.data)
            },
            onError: error => {
                console.log(error + ' error')
            },
            onSubscribe: cancel => {
                console.log('subscribe join chat')
                // console.log(cancel)
            }
        });
    }

    sendMessage(userMetadataProvider: AuthMetadataProvider, message : InputMessage, onComplete : (message : Message) => void) {
        console.log("sending")
        console.log(JSON.stringify(message))
        const metadata = encodeCompositeMetadata(
            [
                [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("send-message")],
                [MESSAGE_RSOCKET_AUTHENTICATION.string, userMetadataProvider.userMetadata()]
            ]
        )
        this.rsocket.requestResponse({
            data: Buffer.from(JSON.stringify(message)),
            metadata: metadata
        }).subscribe({
            onComplete: data => {
                const message : Message = JSON.parse(data.data)
                onComplete(message)
                console.log('message received when sending ' + message)
            },
            onError: error => {
                console.log(error + ' error')
            },
            onSubscribe: cancel => {
                console.log('subscribe send message')
                console.log(cancel)
            }
        });
    } 

    messageStream(userMetadataProvider: AuthMetadataProvider, onNextMessage : (message : Message) => void) {
        const metadata = encodeCompositeMetadata(
            [
                [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("messages-stream")],
                [MESSAGE_RSOCKET_AUTHENTICATION.string, userMetadataProvider.userMetadata()]
            ]
        )

        console.log("message stream client")
        // var d : ISubscription = null;
        this.rsocket.requestStream({
            metadata: metadata
        }).subscribe({
            onComplete: () => {
                console.log("stream on complete")
            },
            onNext : (data) => {
                console.log("on message")
                const a : Message = JSON.parse(data.data)
                onNextMessage(a)
            
            },
            onError: error => {
                console.log(error + ' error')
            },
            onSubscribe: subscription => {
                console.log('subscribe to stream')
                // console.log(cancel)
                // d = subscription
                subscription.request(100000000)
            }
        });
    } 

    disconnect() {
        this.rsocket.close()
    }
}

export { ChatServerClient };
