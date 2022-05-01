import React, { FC, useEffect, useRef, useState } from 'react';
import { BufferEncoders, encodeCompositeMetadata, encodeRoute, encodeSimpleAuthMetadata, MESSAGE_RSOCKET_AUTHENTICATION, MESSAGE_RSOCKET_COMPOSITE_METADATA, MESSAGE_RSOCKET_ROUTING, RSocketClient } from 'rsocket-core';
import { ReactiveSocket } from 'rsocket-types';
import RSocketWebsocketClient from 'rsocket-websocket-client';
import { ChatServerClient } from '../../lib/chat-server-client/ChatServerClient';
import { SimpleAuthUserMetadataProvider } from '../../lib/chat-server-client/SimpleAuthUserMetadataProvider';
import ChatInputTextBox from '../ChatInputTextBox/ChatInputTextBox.lazy';
import ChatList from '../ChatList/ChatList';
import ChatMessagesFeed from '../ChatMessagesFeed/ChatMessagesFeed';
import styles from './ChatWindow.module.css';


interface ChatWindowProps { }

const ChatWindow: FC<ChatWindowProps> = (props: ChatWindowProps) => {

  const [chats, setChats] = useState(new Array(0))
  const rsocket = useRef<ChatServerClient | null>(null)

  useEffect(() => {
    async function getClient() {
      const client = await ChatServerClient.CreateAsync("localhost", 9090)
      rsocket.current = client
    }
    getClient()
  })

  const authProvider = new SimpleAuthUserMetadataProvider("user1", "pass")

  const createHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    console.log("in handler")
    rsocket.current?.createChat(authProvider)
  }

  return (
    <div className={styles.ChatWindow} data-testid="ChatWindow">
      <ChatList chatList={chats} />
      <ChatMessagesFeed />
      <ChatInputTextBox send={() => { }} />
      <button onClick={createHandler}>Create new chat</button>
    </div>
  )

};

export default ChatWindow;
