
import { BufferEncoders, encodeCompositeMetadata, encodeRoute, MESSAGE_RSOCKET_AUTHENTICATION, MESSAGE_RSOCKET_COMPOSITE_METADATA, MESSAGE_RSOCKET_ROUTING, PayloadSerializers, RSocketClient } from 'rsocket-core';
import { Flowable } from 'rsocket-flowable';
import { RSocketRequester } from 'rsocket-messaging';
import { ReactiveSocket } from 'rsocket-types';
import RSocketWebsocketClient from 'rsocket-websocket-client';
import { AuthMetadataProvider } from './AuthMetadataProvider';
import { InputMessage } from './InputMessage';
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
            data: Buffer.from("aaa"),
            metadata: metadata
        }).subscribe({
            onComplete: data => {
                onComplete(data.data)
                console.log('data' + data.data)
            },
            onError: error => {
                console.log(error + ' error')
            },
            onSubscribe: cancel => {
                console.log('subscribe')
                console.log(cancel)
            }
        });
    }

    joinChat(userMetadataProvider: AuthMetadataProvider, chatId: String) {
        const metadata = encodeCompositeMetadata(
            [
                [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("join-chat")],
                [MESSAGE_RSOCKET_AUTHENTICATION.string, userMetadataProvider.userMetadata()]
            ]
        )
        this.rsocket.requestResponse({
            data: { 'chatId': chatId },
            metadata: metadata
        }).subscribe({
            onComplete: data => {
                console.log('chat joined' + data.data)
            },
            onError: error => {
                console.log(error + ' error')
            },
            onSubscribe: cancel => {
                console.log('subscribe')
                console.log(cancel)
            }
        });
    }

    channel(userMetadataProvider: AuthMetadataProvider, pushMessage: Flowable<InputMessage>) {
        const metadata = encodeCompositeMetadata(
            [
                [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("chat-channel")],
                [MESSAGE_RSOCKET_AUTHENTICATION.string, userMetadataProvider.userMetadata()]
            ]
        )
        const d = pushMessage.map(m => {
            return {
                data: m,
                metadata: metadata
            }
        })
        return this.rsocket.requestChannel(d)
    }

    test(pushMessage: Flowable<String>) {
        const metadata = encodeCompositeMetadata(
            [
                [MESSAGE_RSOCKET_ROUTING.string, encodeRoute("test")]
            ]
        )
        const payloads = pushMessage.map(message => {
            return {
                data: message,
                metadata: metadata
            }
        })
        return this.rsocket.requestChannel(payloads)
    }

}


export { ChatServerClient };
