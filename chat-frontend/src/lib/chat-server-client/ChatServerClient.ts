
import { BufferEncoders, encodeCompositeMetadata, encodeRoute, encodeSimpleAuthMetadata, MESSAGE_RSOCKET_AUTHENTICATION, MESSAGE_RSOCKET_COMPOSITE_METADATA, MESSAGE_RSOCKET_ROUTING, RSocketClient } from 'rsocket-core';
import { ReactiveSocket } from 'rsocket-types';
import RSocketWebsocketClient from 'rsocket-websocket-client';
import { AuthMetadataProvider } from './AuthMetadataProvider';

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


    createChat(userMetadataProvider: AuthMetadataProvider) {
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

}

export { ChatServerClient };